. "$PSScriptRoot\common.ps1"

Enter-ProjectRoot
Write-Step 'Preparing project-local Microsoft OpenJDK 21'

$toolsDir = Join-Path $script:ProjectRoot '.tools'
$jdkHome = Join-Path $toolsDir 'jdk-21'
$javaExecutable = Join-Path $jdkHome 'bin\java.exe'

if (-not (Test-Path -LiteralPath $javaExecutable)) {
    New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null

    $downloadUrl = 'https://aka.ms/download-jdk/microsoft-jdk-21-windows-x64.zip'
    $checksumUrl = "$downloadUrl.sha256sum.txt"
    $archive = Join-Path $toolsDir 'microsoft-jdk-21-windows-x64.zip'
    $checksumFile = "$archive.sha256sum.txt"
    $expandDir = Join-Path $toolsDir 'jdk-expand'

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
        if (-not $resolvedExpand.StartsWith($toolsDir, [System.StringComparison]::OrdinalIgnoreCase)) {
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
Invoke-ProjectGradle '--version'

Write-Step 'Downloading dependencies and compiling the mod'
Invoke-ProjectGradle 'compileJava'

Write-Host ''
Write-Host 'Setup complete. Run play.cmd to launch the development client.' -ForegroundColor Green
