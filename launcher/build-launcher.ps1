[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$launcherRoot = (Resolve-Path -LiteralPath $PSScriptRoot).Path
$projectRoot = (Resolve-Path -LiteralPath (Join-Path $launcherRoot '..')).Path
. (Join-Path $projectRoot 'scripts\common.ps1')
$javaHome = Use-ProjectJava

$javac = Join-Path $javaHome 'bin\javac.exe'
$jar = Join-Path $javaHome 'bin\jar.exe'
$jpackage = Join-Path $javaHome 'bin\jpackage.exe'
foreach ($tool in @($javac, $jar, $jpackage)) {
    if (-not (Test-Path -LiteralPath $tool)) {
        throw "Required JDK tool is missing: $tool"
    }
}

$buildRoot = Join-Path $launcherRoot 'build'
$classes = Join-Path $buildRoot 'classes'
$inputDir = Join-Path $buildRoot 'input'
$distRoot = Join-Path $launcherRoot 'dist'
$appName = 'Gravesown Launcher'
$exePath = Join-Path $distRoot "$appName\$appName.exe"

$backgroundGenerator = Join-Path $projectRoot 'scripts\generate-presentation-background.ps1'
if (-not (Test-Path -LiteralPath $backgroundGenerator -PathType Leaf)) {
    throw "Required presentation background generator is missing: $backgroundGenerator"
}
Write-Host '==> Regenerating cold navy presentation background' -ForegroundColor Cyan
& $backgroundGenerator

foreach ($target in @($buildRoot, $distRoot)) {
    $resolvedParent = (Resolve-Path -LiteralPath (Split-Path -Parent $target)).Path
    if ($resolvedParent -ne $launcherRoot) {
        throw "Refusing to clean a path outside launcher/: $target"
    }
    if (Test-Path -LiteralPath $target) {
        $targetItem = Get-Item -LiteralPath $target -Force
        if (($targetItem.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -ne 0) {
            throw "Refusing to recursively clean a launcher path that is a junction or symbolic link: $target"
        }
        Remove-Item -LiteralPath $target -Recurse -Force
    }
}

New-Item -ItemType Directory -Force -Path $classes, $inputDir, $distRoot | Out-Null
$sources = @(Get-ChildItem -LiteralPath (Join-Path $launcherRoot 'src') -Recurse -File -Filter '*.java')
if ($sources.Count -eq 0) {
    throw 'No launcher Java sources were found.'
}

Write-Host '==> Compiling Gravesown launcher' -ForegroundColor Cyan
& $javac --release 21 -encoding UTF-8 -d $classes @($sources.FullName)
if ($LASTEXITCODE -ne 0) { throw "javac failed with exit code $LASTEXITCODE" }

$background = Join-Path $launcherRoot 'assets\launcher_background.png'
if (Test-Path -LiteralPath $background) {
    Copy-Item -LiteralPath $background -Destination (Join-Path $classes 'launcher_background.png') -Force
    Write-Host 'Included launcher/assets/launcher_background.png'
}

$iconPng = Join-Path $launcherRoot 'assets\launcher_icon.png'
$iconIco = Join-Path $buildRoot 'launcher_icon.ico'
if (Test-Path -LiteralPath $iconPng) {
    Copy-Item -LiteralPath $iconPng -Destination (Join-Path $classes 'launcher_icon.png') -Force

    # Windows ICO supports an embedded PNG payload. This keeps the source icon
    # editable as PNG while giving jpackage a native executable icon.
    $pngBytes = [System.IO.File]::ReadAllBytes($iconPng)
    $stream = New-Object System.IO.MemoryStream
    $writer = New-Object System.IO.BinaryWriter($stream)
    try {
        $writer.Write([uint16]0)
        $writer.Write([uint16]1)
        $writer.Write([uint16]1)
        $writer.Write([byte]64)
        $writer.Write([byte]64)
        $writer.Write([byte]0)
        $writer.Write([byte]0)
        $writer.Write([uint16]1)
        $writer.Write([uint16]32)
        $writer.Write([uint32]$pngBytes.Length)
        $writer.Write([uint32]22)
        $writer.Write($pngBytes)
        $writer.Flush()
        [System.IO.File]::WriteAllBytes($iconIco, $stream.ToArray())
    }
    finally {
        $writer.Dispose()
        $stream.Dispose()
    }
}
else {
    Write-Host 'INFO Launcher icon absent; the default application icon will be used.' -ForegroundColor DarkYellow
}

$launcherJar = Join-Path $inputDir 'gravesown-launcher.jar'
Write-Host '==> Creating launcher JAR' -ForegroundColor Cyan
& $jar --create --file $launcherJar --main-class dev.gravesown.launcher.GravesownLauncher -C $classes .
if ($LASTEXITCODE -ne 0) { throw "jar failed with exit code $LASTEXITCODE" }

Write-Host '==> Creating local Windows app-image' -ForegroundColor Cyan
& $jpackage `
    --type app-image `
    --dest $distRoot `
    --input $inputDir `
    --name $appName `
    --main-jar 'gravesown-launcher.jar' `
    --main-class 'dev.gravesown.launcher.GravesownLauncher' `
    --app-version '0.1.0' `
    --vendor 'Gravesown' `
    --description 'Gravesown: After the Silence development launcher' `
    --add-modules 'java.desktop' `
    $(if (Test-Path -LiteralPath $iconIco) { '--icon'; $iconIco }) `
    --java-options '-Dfile.encoding=UTF-8'
if ($LASTEXITCODE -ne 0) { throw "jpackage failed with exit code $LASTEXITCODE" }

if (-not (Test-Path -LiteralPath $exePath)) {
    throw "jpackage completed but the launcher executable is missing: $exePath"
}

Write-Host ''
Write-Host 'Launcher build: PASS' -ForegroundColor Green
Write-Host "Executable: $exePath" -ForegroundColor Green
