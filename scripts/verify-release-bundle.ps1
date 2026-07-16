param(
    [string]$ZipPath
)

. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot

if ([string]::IsNullOrWhiteSpace($ZipPath)) {
    $ZipPath = Get-ChildItem -LiteralPath (Join-Path $script:ProjectRoot 'release') `
        -Filter 'Gravesown-After-the-Silence-*-Windows.zip' -File |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}
if ([string]::IsNullOrWhiteSpace($ZipPath) -or
    -not (Test-Path -LiteralPath $ZipPath -PathType Leaf)) {
    throw "Release ZIP is missing: $ZipPath"
}

$verifyName = 'release-bundle-verify-{0}-{1}' -f $PID, (Get-Date -Format 'yyyyMMddHHmmssfff')
$verifyRoot = Join-Path $script:ProjectRoot (Join-Path 'build\tmp' $verifyName)
$verifyRootFull = [System.IO.Path]::GetFullPath($verifyRoot)
if (-not (Test-PathInside -Parent $script:ProjectRoot -Child $verifyRootFull)) {
    throw "Unsafe release verification path: $verifyRootFull"
}
New-Item -ItemType Directory -Force -Path $verifyRootFull | Out-Null

try {
    Expand-Archive -LiteralPath $ZipPath -DestinationPath $verifyRootFull -Force
    $bundleRoot = Join-Path $verifyRootFull 'Gravesown After the Silence'
    if (-not (Test-Path -LiteralPath $bundleRoot -PathType Container)) {
        throw 'Release ZIP does not contain the expected Gravesown root directory.'
    }

    $properties = @{}
    Get-Content -LiteralPath (Join-Path $bundleRoot 'gradle.properties') -Encoding UTF8 |
        Where-Object { $_ -match '^[^#=]+=' } |
        ForEach-Object {
            $key, $value = $_ -split '=', 2
            $properties[$key.Trim()] = $value.Trim()
        }
    $version = $properties.mod_version

    $required = @(
        'START_HERE.txt',
        'AGENTS.md',
        'README.md',
        'gradlew.bat',
        'setup.cmd',
        'launcher.cmd',
        'scripts\bootstrap-and-play.ps1',
        "dist\gravesown-$version.jar",
        'launcher\dist\Gravesown Launcher\Gravesown Launcher.exe'
    )
    foreach ($relative in $required) {
        if (-not (Test-Path -LiteralPath (Join-Path $bundleRoot $relative) -PathType Leaf)) {
            throw "Release bundle is missing: $relative"
        }
    }

    $forbiddenDirectoryNames = @(
        '.git', '.gradle', '.gradle-user-home', '.tools',
        'run', 'run-worldtest', 'run-clienttest', 'runs',
        'logs', 'crash-reports'
    )
    $forbiddenDirectories = @(
        Get-ChildItem -LiteralPath $bundleRoot -Recurse -Force -Directory |
            Where-Object { $_.Name -in $forbiddenDirectoryNames }
    )
    $forbiddenFileNames = @(
        'eula.txt', 'servers.dat', 'launcher_accounts.json',
        'usercache.json', 'usernamecache.json'
    )
    $forbiddenFiles = @(
        Get-ChildItem -LiteralPath $bundleRoot -Recurse -Force -File |
            Where-Object { $_.Name -in $forbiddenFileNames }
    )
    if ($forbiddenDirectories -or $forbiddenFiles) {
        $paths = @($forbiddenDirectories.FullName) + @($forbiddenFiles.FullName)
        throw "Private or generated runtime state exists in the release bundle:`n$($paths -join "`n")"
    }

    $packagedJar = Join-Path $bundleRoot "dist\gravesown-$version.jar"
    $originalJar = Join-Path $script:ProjectRoot "dist\gravesown-$version.jar"
    $packagedHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $packagedJar).Hash
    $originalHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $originalJar).Hash
    if ($packagedHash -ne $originalHash) {
        throw 'The JAR inside the release bundle does not match dist/.'
    }

    $launcherExe = Join-Path $bundleRoot 'launcher\dist\Gravesown Launcher\Gravesown Launcher.exe'
    $launcherDiagnostic = Start-Process -FilePath $launcherExe -ArgumentList '--diagnose' `
        -WindowStyle Hidden -Wait -PassThru
    if ($launcherDiagnostic.ExitCode -ne 0) {
        throw "Packaged launcher diagnostic failed with exit code $($launcherDiagnostic.ExitCode)."
    }

    $files = @(Get-ChildItem -LiteralPath $bundleRoot -Recurse -Force -File)
    $bytes = ($files | Measure-Object -Property Length -Sum).Sum
    Write-Host ('RELEASE BUNDLE VERIFY PASS: {0} files, {1:N1} MiB, JAR {2}' -f
        $files.Count, ($bytes / 1MB), $packagedHash) -ForegroundColor Green
}
finally {
    if (Test-Path -LiteralPath $verifyRootFull) {
        $removed = $false
        for ($attempt = 1; $attempt -le 5 -and -not $removed; $attempt++) {
            try {
                Remove-Item -LiteralPath $verifyRootFull -Recurse -Force -ErrorAction Stop
                $removed = $true
            }
            catch {
                if ($attempt -lt 5) {
                    Start-Sleep -Milliseconds 250
                }
                else {
                    Write-Warning "Could not remove ignored verification directory yet: $verifyRootFull"
                }
            }
        }
    }
}
