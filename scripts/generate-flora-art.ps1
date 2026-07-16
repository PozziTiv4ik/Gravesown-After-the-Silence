Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

function New-Canvas([string]$RelativePath) {
    $path = Join-Path $script:ProjectRoot $RelativePath
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
    return @($path, (New-Object System.Drawing.Bitmap 16, 16))
}

function Color([string]$hex) { [System.Drawing.ColorTranslator]::FromHtml($hex) }
function Pixel($bitmap, [int]$x, [int]$y, [string]$hex) { $bitmap.SetPixel($x, $y, (Color $hex)) }
function Save($pair) { $pair[1].Save($pair[0], [System.Drawing.Imaging.ImageFormat]::Png); $pair[1].Dispose() }

$cinder = New-Canvas 'src\main\resources\assets\gravesown\textures\block\cinder_bloom.png'
foreach($p in @(@(7,15),@(8,15),@(7,14),@(8,14),@(7,13),@(8,13),@(6,12),@(9,12))){Pixel $cinder[1] $p[0] $p[1] '#607849'}
foreach($p in @(@(5,11),@(6,10),@(7,11),@(8,10),@(9,11),@(10,10),@(7,9),@(8,9))){Pixel $cinder[1] $p[0] $p[1] '#B94832'}
foreach($p in @(@(6,9),@(9,9),@(7,8),@(8,8))){Pixel $cinder[1] $p[0] $p[1] '#E08B3E'}
Pixel $cinder[1] 7 10 '#F3C25B'; Pixel $cinder[1] 8 10 '#F3C25B'; Save $cinder

$fern = New-Canvas 'src\main\resources\assets\gravesown\textures\block\sinew_fern.png'
for($y=5;$y -le 15;$y++){Pixel $fern[1] 7 $y '#667C51'; Pixel $fern[1] 8 $y '#3D5741'}
foreach($p in @(@(6,13),@(5,12),@(4,11),@(9,12),@(10,11),@(11,10),@(6,10),@(5,9),@(9,8),@(10,7),@(7,6),@(8,5))){Pixel $fern[1] $p[0] $p[1] '#8CA565'}
foreach($p in @(@(4,10),@(11,9),@(5,8),@(10,6))){Pixel $fern[1] $p[0] $p[1] '#A75D67'}; Save $fern

$reed = New-Canvas 'src\main\resources\assets\gravesown\textures\block\marrow_reed.png'
foreach($x in 5,8,11){for($y=5+$x%3;$y -le 15;$y++){Pixel $reed[1] $x $y '#8A7658'}}
foreach($p in @(@(4,7),@(5,6),@(6,7),@(7,5),@(8,4),@(9,5),@(10,8),@(11,7),@(12,8))){Pixel $reed[1] $p[0] $p[1] '#D5C49A'}
foreach($p in @(@(5,5),@(8,3),@(11,6))){Pixel $reed[1] $p[0] $p[1] '#F0E0B2'}; Save $reed

$dust = New-Canvas 'src\main\resources\assets\gravesown\textures\item\gravebloom_dust.png'
foreach($p in @(@(4,11),@(5,10),@(6,12),@(7,9),@(8,11),@(9,8),@(10,10),@(11,12),@(6,7),@(8,6),@(10,5),@(12,7),@(3,8))){Pixel $dust[1] $p[0] $p[1] '#B9C77A'}
foreach($p in @(@(5,11),@(7,8),@(9,10),@(11,6),@(8,5))){Pixel $dust[1] $p[0] $p[1] '#E5DA9A'}
foreach($p in @(@(6,10),@(10,9),@(4,7))){Pixel $dust[1] $p[0] $p[1] '#7E985A'}; Save $dust

Write-Host 'PASS generated clustered Minecraft-style Gravebloom flora art.'
