Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Add-Type -AssemblyName System.Drawing

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path

function New-Brush([string]$hex) {
    $color = [System.Drawing.ColorTranslator]::FromHtml($hex)
    return New-Object System.Drawing.SolidBrush($color)
}

function Fill($graphics, $brush, [int]$x, [int]$y, [int]$w, [int]$h) {
    $graphics.FillRectangle($brush, $x, $y, $w, $h)
}

function Save-Png($bitmap, [string]$relativePath) {
    $path = Join-Path $projectRoot $relativePath
    $directory = Split-Path -Parent $path
    if (-not (Test-Path -LiteralPath $directory)) {
        New-Item -ItemType Directory -Force -Path $directory | Out-Null
    }
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Wrote $relativePath"
}

$p = @{
    void = New-Brush '#181817'
    earth = New-Brush '#302D28'
    earthLight = New-Brush '#474137'
    earthDark = New-Brush '#24231F'
    bone = New-Brush '#B8AA83'
    boneLight = New-Brush '#D0C39B'
    boneDark = New-Brush '#746B54'
    sinew = New-Brush '#743F3E'
    sinewLight = New-Brush '#A45A50'
    mold = New-Brush '#69763C'
    grave = New-Brush '#393A37'
    graveLight = New-Brush '#5A5A52'
    graveDark = New-Brush '#222421'
}

try {
    $block = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($block)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        Fill $g $p.graveDark 0 0 16 16
        Fill $g $p.grave 1 1 14 14
        Fill $g $p.graveLight 2 1 10 2
        Fill $g $p.earthDark 1 13 14 2
        Fill $g $p.boneDark 4 4 8 1
        Fill $g $p.bone 7 3 2 8
        Fill $g $p.bone 4 6 8 2
        Fill $g $p.void 6 5 1 1
        Fill $g $p.void 9 5 1 1
        Fill $g $p.sinew 3 10 10 1
        Fill $g $p.graveDark 5 11 1 3
        Fill $g $p.graveDark 10 8 1 4
        Fill $g $p.mold 2 3 2 2
        Fill $g $p.mold 12 11 2 2
    }
    finally {
        $g.Dispose()
    }
    Save-Png $block 'src/main/resources/assets/gravesown/textures/block/remnant_grave.png'
    $block.Dispose()

    $entity = New-Object System.Drawing.Bitmap(128, 128, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($entity)
    try {
        $g.Clear([System.Drawing.Color]::FromArgb(255, 36, 35, 31))

        # Opaque safety underlay: soil mottling prevents transparent fallback faces.
        for ($y = 0; $y -lt 128; $y += 4) {
            for ($x = 0; $x -lt 128; $x += 4) {
                if ((($x / 4) + ($y / 4)) % 3 -eq 0) {
                    Fill $g $p.earth  $x $y 3 3
                }
                elseif ((($x / 4) * 3 + ($y / 4)) % 5 -eq 0) {
                    Fill $g $p.earthLight $x $y 2 2
                }
            }
        }

        # Split burial mask and chipped skull cap.
        Fill $g $p.boneDark 0 0 32 16
        Fill $g $p.bone 2 2 28 12
        Fill $g $p.earthDark 15 1 3 14
        Fill $g $p.void 5 5 6 3
        Fill $g $p.void 21 6 5 3
        Fill $g $p.mold 3 2 5 2
        Fill $g $p.sinew 10 11 14 2
        Fill $g $p.boneLight 2 3 2 8
        Fill $g $p.boneDark 28 4 2 8
        Fill $g $p.graveDark 32 0 36 12
        Fill $g $p.grave 34 2 32 8
        Fill $g $p.boneDark 41 1 4 10
        Fill $g $p.mold 58 4 6 3

        # Wet crooked jaw.
        Fill $g $p.sinew 0 16 24 9
        Fill $g $p.sinewLight 2 17 20 2
        Fill $g $p.void 3 20 18 4
        foreach ($x in @(4, 7, 11, 16, 19)) {
            Fill $g $p.bone $x 20 2 (2 + ($x % 2))
        }

        # Torso of packed loam, exposed ribs and the separate spine.
        Fill $g $p.earthDark 24 16 28 17
        Fill $g $p.earth 26 18 24 13
        foreach ($y in @(20, 24, 28)) {
            Fill $g $p.boneDark 27 $y 20 1
            Fill $g $p.bone 28 ($y - 1) 2 3
            Fill $g $p.bone 44 ($y - 1) 2 3
        }
        Fill $g $p.sinew 35 17 3 15
        Fill $g $p.boneDark 52 16 8 14
        Fill $g $p.bone 54 17 3 12
        Fill $g $p.void 55 20 1 2

        # Mismatched shoulder stones and cracked chest slab.
        Fill $g $p.graveDark 60 16 18 9
        Fill $g $p.graveLight 62 17 13 2
        Fill $g $p.mold 68 21 7 3
        Fill $g $p.boneDark 78 16 14 9
        Fill $g $p.bone 80 17 10 6
        Fill $g $p.sinew 84 18 2 6
        Fill $g $p.graveDark 92 16 18 6
        Fill $g $p.grave 94 17 14 4
        Fill $g $p.sinew 98 19 8 1

        # Long asymmetrical arms: soil, bone splints, sinew and roots.
        Fill $g $p.earthDark 0 32 16 17
        Fill $g $p.earth 2 34 12 13
        Fill $g $p.sinew 4 35 2 11
        Fill $g $p.graveDark 16 32 20 10
        Fill $g $p.graveLight 18 33 16 3
        Fill $g $p.boneDark 20 37 12 3
        Fill $g $p.bone 36 32 4 6
        Fill $g $p.earthDark 40 32 12 18
        Fill $g $p.sinew 44 33 2 16
        Fill $g $p.boneDark 52 32 16 11
        Fill $g $p.bone 54 34 12 7
        Fill $g $p.mold 59 35 5 3
        Fill $g $p.boneDark 68 32 4 6

        # Uneven legs, grave-soil boots and knotted hip growth.
        Fill $g $p.earthDark 72 32 16 16
        Fill $g $p.earthLight 74 34 12 11
        Fill $g $p.sinew 80 34 2 12
        Fill $g $p.graveDark 88 32 22 9
        Fill $g $p.graveLight 90 34 17 3
        Fill $g $p.mold 102 37 5 3
        Fill $g $p.earthDark 0 52 14 16
        Fill $g $p.earth 2 54 10 12
        Fill $g $p.boneDark 6 55 3 10
        Fill $g $p.graveDark 14 52 20 9
        Fill $g $p.graveLight 16 53 16 3
        Fill $g $p.boneDark 34 52 18 6
        Fill $g $p.bone 37 53 12 3
        Fill $g $p.sinew 43 53 2 5

        # Dangling root-strands own narrow dedicated islands.
        Fill $g $p.earthDark 52 52 12 10
        Fill $g $p.sinew 53 53 2 8
        Fill $g $p.mold 57 54 2 7
        Fill $g $p.boneDark 61 53 2 6

        # Sparse hard-pixel scars across occupied areas.
        foreach ($point in @(
            @(7, 3), @(12, 8), @(26, 4), @(38, 5), @(50, 9), @(64, 3),
            @(29, 22), @(40, 19), @(47, 29), @(66, 18), @(83, 22), @(101, 18),
            @(5, 38), @(12, 45), @(25, 35), @(45, 42), @(61, 38), @(78, 44),
            @(96, 35), @(7, 60), @(22, 57), @(42, 55), @(56, 58)
        )) {
            Fill $g $p.boneLight $point[0] $point[1] 1 1
        }
    }
    finally {
        $g.Dispose()
    }
    Save-Png $entity 'src/main/resources/assets/gravesown/textures/entity/buried_remnant.png'
    $entity.Dispose()
}
finally {
    foreach ($brush in $p.Values) {
        $brush.Dispose()
    }
}
