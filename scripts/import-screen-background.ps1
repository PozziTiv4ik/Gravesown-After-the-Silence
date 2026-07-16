param(
    [Parameter(Mandatory = $true)]
    [string]$Source
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

$resolvedSource = (Resolve-Path -LiteralPath $Source).Path
$image = [System.Drawing.Image]::FromFile($resolvedSource)
try {
    if ($image.Width -ne 1672 -or $image.Height -ne 941) {
        throw "Expected a 1672x941 screen background, got $($image.Width)x$($image.Height)"
    }
}
finally {
    $image.Dispose()
}

$sourceMaster = Join-Path $script:ProjectRoot 'launcher\assets\launcher_background_source.png'
$generatedBackground = Join-Path $script:ProjectRoot 'launcher\assets\launcher_background.png'
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $sourceMaster) | Out-Null
if ([System.IO.Path]::GetFullPath($resolvedSource) -eq [System.IO.Path]::GetFullPath($generatedBackground)) {
    throw 'Refusing to import the derived launcher_background.png as a new source master.'
}
if ([System.IO.Path]::GetFullPath($resolvedSource) -ne [System.IO.Path]::GetFullPath($sourceMaster)) {
    Copy-Item -LiteralPath $resolvedSource -Destination $sourceMaster -Force
}

$generator = Join-Path $PSScriptRoot 'generate-presentation-background.ps1'
& $generator

$graded = Join-Path $script:ProjectRoot 'launcher\assets\launcher_background.png'
foreach ($relativeTarget in @(
    'src\main\resources\assets\gravesown\textures\gui\screen_background.png',
    'src\main\resources\assets\gravesown\textures\gui\title_background.png'
)) {
    $destination = Join-Path $script:ProjectRoot $relativeTarget
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $destination) | Out-Null
    Copy-Item -LiteralPath $graded -Destination $destination -Force
    Write-Host "Imported graded background: $destination"
}
