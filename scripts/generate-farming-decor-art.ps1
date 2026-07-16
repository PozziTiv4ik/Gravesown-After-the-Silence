Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

function C([string]$hex) { [System.Drawing.ColorTranslator]::FromHtml($hex) }
function New-Bitmap { [System.Drawing.Bitmap]::new(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb) }
function Fill($g, [string]$hex, [int]$x, [int]$y, [int]$w, [int]$h) {
    $brush = [System.Drawing.SolidBrush]::new((C $hex))
    try { $g.FillRectangle($brush, $x, $y, $w, $h) } finally { $brush.Dispose() }
}
function Save-Art($bitmap, [string]$relative) {
    $path = Join-Path $script:ProjectRoot $relative
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Created $relative"
}
function Paint([string]$relative, [scriptblock]$body, [bool]$transparent = $false) {
    $bitmap = New-Bitmap
    $g = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        if ($transparent) { $g.Clear([System.Drawing.Color]::Transparent) }
        & $body $bitmap $g
        Save-Art $bitmap $relative
    }
    finally { $g.Dispose(); $bitmap.Dispose() }
}

$blockRoot = 'src/main/resources/assets/gravesown/textures/block'
$itemRoot = 'src/main/resources/assets/gravesown/textures/item'

Paint "$blockRoot/ribroot_door_bottom.png" {
    param($b,$g)
    Fill $g '#24191F' 0 0 16 16; Fill $g '#4A2E3A' 1 0 14 16
    Fill $g '#2D1B24' 3 0 2 16; Fill $g '#2D1B24' 11 0 2 16
    Fill $g '#6A4552' 5 2 6 2; Fill $g '#302029' 5 5 6 8
    Fill $g '#8D7D5B' 7 7 2 4; Fill $g '#B9A773' 8 8 1 2
    Fill $g '#713D42' 2 12 12 2; $b.SetPixel(12, 7, (C '#9B865A'))
}
Paint "$blockRoot/ribroot_door_top.png" {
    param($b,$g)
    Fill $g '#24191F' 0 0 16 16; Fill $g '#4A2E3A' 1 0 14 16
    Fill $g '#2D1B24' 3 0 2 16; Fill $g '#2D1B24' 11 0 2 16
    Fill $g '#713D42' 2 3 12 2; Fill $g '#302029' 5 6 6 8
    Fill $g '#171319' 6 7 4 4; Fill $g '#78864A' 7 8 2 2
    Fill $g '#B9A773' 5 13 6 1
}
Paint "$blockRoot/ribroot_trapdoor.png" {
    param($b,$g)
    Fill $g '#24191F' 0 0 16 16; Fill $g '#4A2E3A' 1 1 14 14
    Fill $g '#2D1B24' 3 3 10 10; Fill $g '#171319' 5 5 6 6
    Fill $g '#713D42' 1 7 14 2; Fill $g '#8D7D5B' 7 2 2 3
    Fill $g '#B9A773' 8 3 1 1; Fill $g '#78864A' 7 7 2 2
}
Paint "$blockRoot/tallow_lantern.png" {
    param($b,$g)
    Fill $g '#211A1B' 0 0 16 16; Fill $g '#4A3430' 1 1 14 14
    Fill $g '#2A2321' 3 2 10 12; Fill $g '#81714F' 5 3 6 10
    Fill $g '#C4AE69' 6 4 4 8; Fill $g '#F2DA86' 7 5 2 6
    Fill $g '#713D42' 2 7 12 2; Fill $g '#141416' 0 0 16 2
    Fill $g '#141416' 0 14 16 2
}

foreach ($wet in @($false, $true)) {
    $name = if ($wet) { 'gloam_farmland_moist' } else { 'gloam_farmland' }
    Paint "$blockRoot/$name.png" {
        param($b,$g)
        $base = if ($wet) { '#263B31' } else { '#4A3735' }
        $groove = if ($wet) { '#14241F' } else { '#2A2021' }
        $edge = if ($wet) { '#63744A' } else { '#725052' }
        Fill $g $base 0 0 16 16
        foreach ($y in @(2,6,10,14)) { Fill $g $groove 0 $y 16 2; Fill $g $edge (($y * 3) % 7) ($y - 1) 7 1 }
        foreach ($p in @(@(2,4),@(11,5),@(6,9),@(14,12))) { $b.SetPixel($p[0],$p[1],(C '#8B774F')) }
    }
}

function Paint-Crop([string]$name, [int]$age, [bool]$bean) {
    Paint "$blockRoot/${name}_stage$age.png" {
        param($b,$g)
        $height = 3 + [int]([Math]::Floor($age * 12 / 7))
        $top = 16 - $height
        Fill $g '#39482E' 7 $top 2 $height
        Fill $g '#6F7D45' 8 ($top + 1) 1 ([Math]::Max(1,$height - 2))
        if ($age -ge 2) { Fill $g '#4E6138' 4 ($top + 3) 3 2; Fill $g '#4E6138' 9 ($top + 5) 4 2 }
        if ($age -ge 4) { Fill $g '#71814B' 3 ($top + 6) 4 2; Fill $g '#71814B' 9 ($top + 2) 3 2 }
        if ($age -ge 6) {
            $fruit = if ($bean) { '#8A4C4C' } else { '#B5A56A' }
            Fill $g $fruit 5 ($top + 1) 2 3; Fill $g $fruit 9 ($top + 4) 2 3
            if ($age -eq 7) { $b.SetPixel(6,$top,(C '#DDD095')); $b.SetPixel(10,$top+3,(C '#DDD095')) }
        }
    } $true
}
foreach ($age in 0..7) { Paint-Crop 'ashgrain' $age $false; Paint-Crop 'mirebean' $age $true }

Paint "$itemRoot/ribroot_door.png" {
    param($b,$g)
    $g.Clear([System.Drawing.Color]::Transparent); Fill $g '#24191F' 3 1 10 14
    Fill $g '#4A2E3A' 4 1 8 13; Fill $g '#171319' 6 4 4 5
    Fill $g '#78864A' 7 6 2 2; Fill $g '#B9A773' 10 9 1 1
} $true

foreach ($seed in @('ashgrain_seeds','mirebean_seeds')) {
    Paint "$itemRoot/$seed.png" {
        param($b,$g)
        $g.Clear([System.Drawing.Color]::Transparent)
        $fruit = if ($seed -eq 'mirebean_seeds') { '#8A4C4C' } else { '#B5A56A' }
        foreach ($p in @(@(4,10),@(7,6),@(10,9),@(8,12))) { Fill $g $fruit $p[0] $p[1] 2 2 }
        Fill $g '#4E6138' 7 4 2 3
    } $true
}
Paint "$itemRoot/ashgrain.png" { param($b,$g) $g.Clear([System.Drawing.Color]::Transparent); Fill $g '#B5A56A' 4 5 8 8; Fill $g '#DDD095' 6 4 4 3; Fill $g '#6F674F' 7 8 2 5 } $true
Paint "$itemRoot/mirebean.png" { param($b,$g) $g.Clear([System.Drawing.Color]::Transparent); Fill $g '#542E35' 3 5 10 8; Fill $g '#8A4C4C' 4 4 8 8; Fill $g '#B66B63' 6 5 4 2; Fill $g '#39482E' 7 2 2 3 } $true
Paint "$itemRoot/ashgrain_loaf.png" { param($b,$g) $g.Clear([System.Drawing.Color]::Transparent); Fill $g '#5A4732' 2 7 12 6; Fill $g '#A68B55' 3 5 10 7; Fill $g '#D0B775' 5 4 6 3; foreach($x in @(5,8,11)){ Fill $g '#6F674F' $x 6 1 4 } } $true
Paint "$itemRoot/field_ration.png" { param($b,$g) $g.Clear([System.Drawing.Color]::Transparent); Fill $g '#202925' 2 4 12 9; Fill $g '#536142' 3 3 10 9; Fill $g '#B5A56A' 4 5 4 5; Fill $g '#8A4C4C' 9 6 3 3; Fill $g '#C0AD7F' 3 11 10 2 } $true

Write-Host 'Farming and decorative art complete.' -ForegroundColor Green
