Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

function C([string]$hex) { [System.Drawing.ColorTranslator]::FromHtml($hex) }
function Bitmap([int]$w,[int]$h) { New-Object System.Drawing.Bitmap $w,$h }
function Brush([string]$hex) { New-Object System.Drawing.SolidBrush (C $hex) }
function Rect($g,$b,[int]$x,[int]$y,[int]$w,[int]$h) { $g.FillRectangle($b,$x,$y,$w,$h) }
function Save($bitmap,[string]$relative) {
    $path=Join-Path $script:ProjectRoot $relative
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
    $bitmap.Save($path,[System.Drawing.Imaging.ImageFormat]::Png)
    $bitmap.Dispose()
}

$hide=Brush '#60483E'; $hideDark=Brush '#382F2B'; $hideLight=Brush '#826257'
$plate=Brush '#343B36'; $plateLight=Brush '#596057'; $bone=Brush '#C1B68E'
$sinew=Brush '#9E4F4A'; $moss=Brush '#7C9856'; $void=Brush '#171C1A'

function Paint-Island($g,[int]$x,[int]$y,[int]$w,[int]$h,$base,$edge,$accent) {
    Rect $g $edge $x $y $w $h
    if($w -gt 2 -and $h -gt 2){Rect $g $base ($x+1) ($y+1) ($w-2) ($h-2)}
    if($w -gt 7){Rect $g $accent ($x+[int]($w/2)) ($y+1) 1 ($h-2)}
    if($h -gt 6){Rect $g $edge ($x+1) ($y+$h-3) ($w-2) 1}
}

$layer1=Bitmap 128 128; $g=[System.Drawing.Graphics]::FromImage($layer1); $g.Clear([System.Drawing.Color]::Transparent)
try {
    Paint-Island $g 0 0 40 12 $hide $hideDark $hideLight
    Paint-Island $g 40 0 24 10 $hideDark $void $sinew
    Paint-Island $g 64 0 18 15 $hide $hideDark $moss
    Paint-Island $g 82 0 18 3 $plate $void $bone
    Paint-Island $g 100 0 8 6 $plate $void $moss
    Paint-Island $g 0 24 19 12 $hide $hideDark $sinew
    Paint-Island $g 20 24 11 14 $plate $void $plateLight
    Paint-Island $g 30 24 19 12 $hideDark $void $sinew
    Paint-Island $g 50 24 8 11 $plate $void $bone
    Paint-Island $g 70 24 21 8 $hideDark $void $bone
    Paint-Island $g 90 24 29 8 $plate $void $moss
    Paint-Island $g 0 48 23 11 $plate $void $plateLight
    Paint-Island $g 24 48 21 11 $hide $hideDark $sinew
    Paint-Island $g 46 48 21 7 $hideDark $void $bone
    Paint-Island $g 68 48 11 12 $plate $void $moss
    Paint-Island $g 0 72 21 12 $hide $hideDark $sinew
    Paint-Island $g 22 72 25 11 $plate $void $bone
    Paint-Island $g 48 72 13 8 $plate $void $plateLight
    Paint-Island $g 62 72 25 9 $hideDark $void $moss
} finally { $g.Dispose() }
Save $layer1 'src\main\resources\assets\gravesown\textures\models\armor\quietskin_layer_1.png'

$layer2=Bitmap 128 128; $g=[System.Drawing.Graphics]::FromImage($layer2); $g.Clear([System.Drawing.Color]::Transparent)
try {
    Paint-Island $g 0 0 29 9 $hideDark $void $bone
    Paint-Island $g 30 0 13 9 $hide $hideDark $sinew
    Paint-Island $g 44 0 13 13 $plate $void $plateLight
    Paint-Island $g 58 0 15 6 $plate $void $bone
    Paint-Island $g 74 0 21 7 $hideDark $void $moss
    Paint-Island $g 96 0 21 7 $hideDark $void $sinew
} finally { $g.Dispose() }
Save $layer2 'src\main\resources\assets\gravesown\textures\models\armor\quietskin_layer_2.png'

function Armor-Icon([string]$name,[string]$kind) {
    $bmp=Bitmap 16 16; $gr=[System.Drawing.Graphics]::FromImage($bmp); $gr.Clear([System.Drawing.Color]::Transparent)
    try {
        if($kind -eq 'hood'){
            Rect $gr $hideDark 3 2 10 2; Rect $gr $hide 2 4 3 8; Rect $gr $hide 11 4 3 8
            Rect $gr $plate 5 3 6 2; Rect $gr $bone 6 3 4 1; Rect $gr $moss 3 9 2 2; Rect $gr $moss 11 9 2 2
        } elseif($kind -eq 'coat'){
            Rect $gr $plate 3 2 10 4; Rect $gr $hide 4 5 8 9; Rect $gr $hideDark 1 4 3 9; Rect $gr $hideDark 12 4 3 9
            Rect $gr $bone 7 3 2 10; Rect $gr $sinew 4 8 8 1; Rect $gr $moss 2 6 2 2; Rect $gr $moss 12 6 2 2
        } elseif($kind -eq 'legs'){
            Rect $gr $hideDark 3 2 10 3; Rect $gr $hide 3 5 4 10; Rect $gr $hide 9 5 4 10
            Rect $gr $plate 4 8 3 3; Rect $gr $plate 9 8 3 3; Rect $gr $bone 4 12 3 1; Rect $gr $bone 9 12 3 1
        } else {
            Rect $gr $hide 2 5 5 8; Rect $gr $hide 9 5 5 8; Rect $gr $plate 1 10 6 4; Rect $gr $plate 9 10 6 4
            Rect $gr $bone 2 11 5 1; Rect $gr $bone 9 11 5 1; Rect $gr $moss 3 7 2 2; Rect $gr $moss 11 7 2 2
        }
    } finally { $gr.Dispose() }
    Save $bmp "src\main\resources\assets\gravesown\textures\item\$name.png"
}

Armor-Icon 'quietskin_hood' 'hood'
Armor-Icon 'quietskin_coat' 'coat'
Armor-Icon 'quietskin_legwraps' 'legs'
Armor-Icon 'quietskin_boots' 'boots'
Write-Host 'PASS generated symmetric open-faced Quietskin art.'
