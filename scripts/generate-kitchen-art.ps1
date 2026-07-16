Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

function Color([string]$hex) { [System.Drawing.ColorTranslator]::FromHtml($hex) }
function Brush([string]$hex) { New-Object System.Drawing.SolidBrush (Color $hex) }
function Fill($graphics, $brush, [int]$x, [int]$y, [int]$width, [int]$height) {
    $graphics.FillRectangle($brush, $x, $y, $width, $height)
}
function Save-Png($bitmap, [string]$relativePath) {
    $path = Join-Path $script:ProjectRoot $relativePath
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Created $relativePath"
}
function New-Sprite([scriptblock]$draw, [string]$relativePath) {
    $bitmap = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        & $draw $graphics
    }
    finally {
        $graphics.Dispose()
    }
    Save-Png $bitmap $relativePath
    $bitmap.Dispose()
}

$p = @{
    outline = Brush '#171318'; shadow = Brush '#272129'; iron = Brush '#4B4D50'; ironLight = Brush '#79796F'
    rust = Brush '#79363A'; rustLight = Brush '#A05048'; bone = Brush '#B6A579'; boneLight = Brush '#DDD09E'
    wood = Brush '#4A2A35'; woodLight = Brush '#754252'; broth = Brush '#6D5931'; brothLight = Brush '#A48645'
    green = Brush '#586B35'; greenLight = Brush '#82944B'; flesh = Brush '#843F45'; fleshLight = Brush '#B85E55'
    blue = Brush '#2D5A5B'; blueLight = Brush '#4B8580'; ember = Brush '#D05C36'; cream = Brush '#D8C58C'
}

try {
    New-Sprite {
        param($g)
        Fill $g $p.outline 0 0 16 16
        Fill $g $p.wood 1 1 14 14
        Fill $g $p.woodLight 2 2 12 2
        Fill $g $p.iron 2 5 12 8
        Fill $g $p.ironLight 3 5 10 2
        Fill $g $p.outline 4 7 8 5
        Fill $g $p.rust 5 8 6 3
        Fill $g $p.ember 7 9 2 2
        Fill $g $p.bone 1 13 14 1
        Fill $g $p.outline 3 14 2 2
        Fill $g $p.outline 11 14 2 2
    } 'src/main/resources/assets/gravesown/textures/block/field_kitchen.png'

    New-Sprite {
        param($g)
        for ($i = 5; $i -le 13; $i++) { Fill $g $p.wood (14 - $i) $i 2 2 }
        Fill $g $p.outline 1 1 9 8
        Fill $g $p.bone 2 2 7 6
        Fill $g $p.boneLight 3 2 5 2
        Fill $g $p.rust 2 6 6 2
        Fill $g $p.outline 8 7 3 3
    } 'src/main/resources/assets/gravesown/textures/item/bone_cleaver.png'

    New-Sprite {
        param($g)
        for ($i = 3; $i -le 13; $i++) { Fill $g $p.iron (14 - $i) $i 2 2 }
        Fill $g $p.ironLight 10 3 2 3
        Fill $g $p.outline 1 1 6 6
        Fill $g $p.iron 2 2 4 4
        Fill $g $p.outline 3 3 2 2
        Fill $g $p.rust 5 5 3 2
    } 'src/main/resources/assets/gravesown/textures/item/stirring_hook.png'

    New-Sprite {
        param($g)
        Fill $g $p.outline 2 4 12 9
        Fill $g $p.iron 3 5 10 7
        Fill $g $p.broth 4 6 8 4
        Fill $g $p.green 5 6 2 2
        Fill $g $p.cream 9 7 2 2
        Fill $g $p.brothLight 6 9 5 1
        Fill $g $p.ironLight 4 12 8 2
    } 'src/main/resources/assets/gravesown/textures/item/mirebean_stew.png'

    New-Sprite {
        param($g)
        Fill $g $p.outline 2 3 12 11
        Fill $g $p.rust 3 4 10 9
        Fill $g $p.iron 4 5 8 2
        Fill $g $p.flesh 4 7 8 4
        Fill $g $p.fleshLight 5 7 5 2
        Fill $g $p.ember 9 9 2 2
        Fill $g $p.bone 5 12 6 1
    } 'src/main/resources/assets/gravesown/textures/item/charred_marrow_pot.png'

    New-Sprite {
        param($g)
        Fill $g $p.outline 2 4 12 10
        Fill $g $p.iron 3 5 10 8
        Fill $g $p.blue 4 6 8 5
        Fill $g $p.blueLight 5 7 6 2
        Fill $g $p.green 4 9 3 2
        Fill $g $p.cream 9 9 2 2
        Fill $g $p.ironLight 4 13 8 1
    } 'src/main/resources/assets/gravesown/textures/item/gloam_chowder.png'

    New-Sprite {
        param($g)
        Fill $g $p.outline 1 6 14 6
        Fill $g $p.blue 2 7 10 4
        Fill $g $p.blueLight 3 7 6 1
        Fill $g $p.cream 9 8 2 2
        Fill $g $p.outline 12 5 3 2
        Fill $g $p.outline 12 11 3 2
        Fill $g $p.rust 4 10 4 1
        Fill $g $p.greenLight 2 8 1 2
    } 'src/main/resources/assets/gravesown/textures/item/needle_sprat.png'
}
finally {
    foreach ($brush in $p.Values) { $brush.Dispose() }
}
