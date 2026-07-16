. "$PSScriptRoot\common.ps1"

Enter-ProjectRoot
Write-Step 'Preparing external Java 21 runtime'

$runtimeDir = Get-GravesownPath 'runtime'
$jdkHome = Join-Path $runtimeDir 'jdk-21'
$javaExecutable = Join-Path $jdkHome 'bin\java.exe'
$legacyJdkHome = Join-Path $script:ProjectRoot '.tools\jdk-21'
$legacyJava = Join-Path $legacyJdkHome 'bin\java.exe'

if (-not (Test-Path -LiteralPath $javaExecutable) -and
    (Test-Path -LiteralPath $legacyJava)) {
    Write-Step 'Migrating the legacy project-local JDK outside the Git workspace'
    New-Item -ItemType Directory -Force -Path $runtimeDir | Out-Null
    Move-Item -LiteralPath $legacyJdkHome -Destination $jdkHome
}

$systemJdkAvailable = $false
if (-not (Test-Path -LiteralPath $javaExecutable)) {
    $javaCommand = Get-Command java -ErrorAction SilentlyContinue
    if ($javaCommand) {
        try {
            $systemHome = Split-Path -Parent (Split-Path -Parent $javaCommand.Source)
            $systemJdkAvailable =
                (Get-JavaMajorVersion -JavaExecutable $javaCommand.Source) -eq 21 -and
                (Test-Path -LiteralPath (Join-Path $systemHome 'bin\javac.exe')) -and
                (Test-Path -LiteralPath (Join-Path $systemHome 'bin\jpackage.exe'))
        }
        catch {
            $systemJdkAvailable = $false
        }
    }
}

if (-not (Test-Path -LiteralPath $javaExecutable) -and -not $systemJdkAvailable) {
    New-Item -ItemType Directory -Force -Path $runtimeDir | Out-Null

    $downloadUrl = 'https://aka.ms/download-jdk/microsoft-jdk-21-windows-x64.zip'
    $checksumUrl = "$downloadUrl.sha256sum.txt"
    $archive = Join-Path $runtimeDir 'microsoft-jdk-21-windows-x64.zip'
    $checksumFile = "$archive.sha256sum.txt"
    $expandDir = Join-Path $runtimeDir 'jdk-expand'

    Write-Step 'Downloading Java 21 and its Microsoft checksum'
    $curl = Get-Command curl.exe -ErrorAction SilentlyContinue
    if ($curl) {
        & $curl.Source --fail --location --retry 3 --silent --show-error --output $archive $downloadUrl
        if ($LASTEXITCODE -ne 0) {
            throw "Java download failed with curl exit code $LASTEXITCODE."
        }
        & $curl.Source --fail --location --retry 3 --silent --show-error --output $checksumFile $checksumUrl
        if ($LASTEXITCODE -ne 0) {
            throw "Checksum download failed with curl exit code $LASTEXITCODE."
        }
    }
    else {
        Invoke-WebRequest -UseBasicParsing -Uri $downloadUrl -OutFile $archive
        Invoke-WebRequest -UseBasicParsing -Uri $checksumUrl -OutFile $checksumFile
    }

    $expectedHash = ((Get-Content -Raw -LiteralPath $checksumFile).Trim() -split '\s+')[0].ToLowerInvariant()
    $actualHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $archive).Hash.ToLowerInvariant()
    if ($actualHash -ne $expectedHash) {
        throw "JDK checksum mismatch. Expected $expectedHash, got $actualHash."
    }

    if (Test-Path -LiteralPath $expandDir) {
        $resolvedExpand = (Resolve-Path -LiteralPath $expandDir).Path
        if (-not $resolvedExpand.StartsWith($runtimeDir, [System.StringComparison]::OrdinalIgnoreCase)) {
            throw "Unsafe temporary path: $resolvedExpand"
        }
        Remove-Item -LiteralPath $resolvedExpand -Recurse -Force
    }

    Expand-Archive -LiteralPath $archive -DestinationPath $expandDir -Force
    $extractedHome = Get-ChildItem -LiteralPath $expandDir -Directory |
        Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'bin\java.exe') } |
        Select-Object -First 1

    if (-not $extractedHome) {
        throw 'Downloaded archive did not contain a valid JDK.'
    }

    if (Test-Path -LiteralPath $jdkHome) {
        Remove-Item -LiteralPath $jdkHome -Recurse -Force
    }
    Move-Item -LiteralPath $extractedHome.FullName -Destination $jdkHome
    Remove-Item -LiteralPath $expandDir -Recurse -Force
    Remove-Item -LiteralPath $archive, $checksumFile -Force
}

$selectedJava = Use-ProjectJava
Write-Host "Using JAVA_HOME=$selectedJava" -ForegroundColor Green

Write-Step 'Checking Gradle Wrapper'
Invoke-ProjectGradle '--version' '--no-daemon'

Write-Step 'Downloading dependencies and compiling the mod'
Invoke-ProjectGradle 'compileJava' '--no-daemon'

$marker = Write-GravesownSetupState

Write-Host ''
Write-Host "Setup complete. External data: $script:GravesownHome" -ForegroundColor Green
Write-Host "Setup marker: $marker" -ForegroundColor Green
Write-Host 'Run launcher.cmd or play.cmd.' -ForegroundColor Green
