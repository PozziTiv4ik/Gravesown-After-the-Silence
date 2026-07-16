[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$output = Join-Path $PSScriptRoot '..\src\main\resources\assets\gravesown\textures\item\survivor_codex.png'
$directory = Split-Path -Parent $output
New-Item -ItemType Directory -Force -Path $directory | Out-Null

$bitmap = [System.Drawing.Bitmap]::new(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$bitmap.SetResolution(96, 96)

function Set-Pixel([int]$x, [int]$y, [string]$hex) {
    $bitmap.SetPixel($x, $y, [System.Drawing.ColorTranslator]::FromHtml($hex))
}

$outline = '#21191B'
$coverDark = '#343229'
$cover = '#4B4A35'
$coverLight = '#626345'
$boneDark = '#746D59'
$bone = '#B0A78A'
$blood = '#6B2F32'
$page = '#C7B997'

for ($y = 2; $y -le 13; $y++) {
    for ($x = 2; $x -le 13; $x++) {
        Set-Pixel $x $y $cover
    }
}
for ($x = 3; $x -le 12; $x++) { Set-Pixel $x 1 $outline; Set-Pixel $x 14 $outline }
for ($y = 3; $y -le 12; $y++) { Set-Pixel 1 $y $outline; Set-Pixel 14 $y $outline }
Set-Pixel 2 2 $outline; Set-Pixel 13 2 $outline; Set-Pixel 2 13 $outline; Set-Pixel 13 13 $outline

for ($y = 3; $y -le 12; $y++) { Set-Pixel 2 $y $coverDark; Set-Pixel 3 $y $coverDark }
for ($x = 4; $x -le 12; $x++) { Set-Pixel $x 2 $coverLight }
for ($x = 4; $x -le 12; $x++) { Set-Pixel $x 13 $page }
Set-Pixel 13 4 $coverDark; Set-Pixel 13 9 $coverDark; Set-Pixel 12 12 $coverDark

for ($y = 4; $y -le 11; $y++) { Set-Pixel 7 $y $boneDark; Set-Pixel 8 $y $bone }
Set-Pixel 6 4 $bone; Set-Pixel 9 4 $boneDark
Set-Pixel 5 5 $bone; Set-Pixel 10 5 $boneDark
Set-Pixel 5 7 $bone; Set-Pixel 10 7 $boneDark
Set-Pixel 5 9 $bone; Set-Pixel 10 9 $boneDark
Set-Pixel 6 11 $bone; Set-Pixel 9 11 $boneDark
Set-Pixel 11 8 $blood; Set-Pixel 12 8 $blood; Set-Pixel 11 9 $blood

$bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
$bitmap.Dispose()
Write-Host "Generated $output"
