[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot

Add-Type -AssemblyName System.Drawing

function Color([string]$hex) {
    return [System.Drawing.ColorTranslator]::FromHtml($hex)
}

function Fill(
    [System.Drawing.Graphics]$graphics,
    [System.Drawing.Brush]$brush,
    [int]$x,
    [int]$y,
    [int]$width,
    [int]$height
) {
    $graphics.FillRectangle($brush, $x, $y, $width, $height)
}

function Paint-HideIsland(
    [System.Drawing.Graphics]$graphics,
    [System.Drawing.Bitmap]$bitmap,
    [hashtable]$brushes,
    [int]$x,
    [int]$y,
    [int]$width,
    [int]$height,
    [string]$base,
    [string]$accent
) {
    Fill $graphics $brushes[$base] $x $y $width $height
    Fill $graphics $brushes.shadow $x $y $width 1
    Fill $graphics $brushes.hideDark $x ($y + $height - 1) $width 1

    if ($width -gt 3 -and $height -gt 3) {
        Fill $graphics $brushes[$accent] ($x + 1) ($y + 1) ($width - 2) 1
        $seamX = $x + [int]($width / 2)
        Fill $graphics $brushes.bruise $seamX ($y + 2) 1 ($height - 3)
        for ($fiberY = $y + 3; $fiberY -lt ($y + $height - 1); $fiberY += 3) {
            $fiberX = $x + 2 + (($fiberY - $y) % [Math]::Max(2, $width - 4))
            $bitmap.SetPixel($fiberX, $fiberY, (Color '#AA554B'))
            if (($fiberX + 2) -lt ($x + $width - 1)) {
                $bitmap.SetPixel($fiberX + 2, $fiberY, (Color '#100D12'))
            }
        }
    }
}

function Paint-BoneIsland(
    [System.Drawing.Graphics]$graphics,
    [System.Drawing.Bitmap]$bitmap,
    [hashtable]$brushes,
    [int]$x,
    [int]$y,
    [int]$width,
    [int]$height
) {
    Fill $graphics $brushes.boneDark $x $y $width $height
    if ($width -gt 2 -and $height -gt 2) {
        Fill $graphics $brushes.bone ($x + 1) ($y + 1) ($width - 2) ($height - 2)
    }
    if ($width -gt 5 -and $height -gt 4) {
        Fill $graphics $brushes.marrow ($x + 2) ($y + 2) ($width - 4) 1
        $crackX = $x + [int]($width / 2)
        Fill $graphics $brushes.shadow $crackX ($y + 3) 1 ([Math]::Max(1, $height - 5))
        for ($crackY = $y + 4; $crackY -lt ($y + $height - 1); $crackY += 4) {
            $bitmap.SetPixel([Math]::Max($x + 1, $crackX - 1), $crackY, (Color '#756A54'))
        }
    }
}

function Paint-HoofIsland(
    [System.Drawing.Graphics]$graphics,
    [System.Drawing.Bitmap]$bitmap,
    [hashtable]$brushes,
    [int]$x,
    [int]$y,
    [int]$width,
    [int]$height
) {
    Fill $graphics $brushes.shadow $x $y $width $height
    if ($width -gt 2 -and $height -gt 2) {
        Fill $graphics $brushes.hideDark ($x + 1) ($y + 1) ($width - 2) ($height - 2)
        Fill $graphics $brushes.plate ($x + 2) ($y + 1) ([Math]::Max(1, $width - 4)) 1
        Fill $graphics $brushes.boneDark ($x + 1) ($y + $height - 3) ($width - 2) 1
        for ($toeX = $x + 4; $toeX -lt ($x + $width - 2); $toeX += 6) {
            Fill $graphics $brushes.shadow $toeX ($y + [int]($height / 2)) 1 ([Math]::Max(1, [int]($height / 2) - 1))
            $bitmap.SetPixel($toeX - 1, $y + 2, (Color '#783A43'))
        }
    }
}

$output = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\textures\entity\stitchtusk.png'
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $output) | Out-Null

$bitmap = [System.Drawing.Bitmap]::new(128, 128, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$bitmap.SetResolution(96, 96)
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.Clear([System.Drawing.Color]::Transparent)

$brushes = @{
    shadow = [System.Drawing.SolidBrush]::new((Color '#100D12'))
    hideDark = [System.Drawing.SolidBrush]::new((Color '#241B20'))
    hide = [System.Drawing.SolidBrush]::new((Color '#3E2C33'))
    plate = [System.Drawing.SolidBrush]::new((Color '#5B3C43'))
    bruise = [System.Drawing.SolidBrush]::new((Color '#783A43'))
    seam = [System.Drawing.SolidBrush]::new((Color '#AA554B'))
    boneDark = [System.Drawing.SolidBrush]::new((Color '#756A54'))
    bone = [System.Drawing.SolidBrush]::new((Color '#AD9D78'))
    marrow = [System.Drawing.SolidBrush]::new((Color '#D2C197'))
    sick = [System.Drawing.SolidBrush]::new((Color '#6D7843'))
}

try {
    # Every ModelPart cube reuses this atlas with vanilla box UV unwrapping. Keep a
    # fully opaque, deliberately textured underlay so small growths, hooves and
    # secondary plates never sample transparent gaps between the hero regions.
    # The pattern is subtle enough to sit behind the hand-painted anatomy while
    # still reading as scarred hide when a reused UV footprint reaches it.
    Fill $graphics $brushes.hideDark 0 0 128 128
    for ($y = 0; $y -lt 128; $y += 4) {
        $phase = (([int]($y / 4)) % 2) * 3
        for ($x = $phase; $x -lt 128; $x += 8) {
            $darkWidth = [Math]::Min(3, 128 - $x)
            Fill $graphics $brushes.shadow $x $y $darkWidth 1

            $fiberX = $x + 4
            if ($fiberX -lt 128) {
                $fiberWidth = [Math]::Min(2, 128 - $fiberX)
                Fill $graphics $brushes.hide $fiberX ($y + 1) $fiberWidth 1
            }
        }
    }
    for ($seamY = 15; $seamY -lt 128; $seamY += 24) {
        for ($seamX = 2; $seamX -lt 128; $seamX += 12) {
            $seamWidth = [Math]::Min(5, 128 - $seamX)
            Fill $graphics $brushes.bruise $seamX $seamY $seamWidth 1
            if (($seamX + 2) -lt 128 -and ($seamY + 1) -lt 128) {
                $bitmap.SetPixel($seamX + 2, $seamY + 1, (Color '#AA554B'))
            }
        }
    }

    # Head and muzzle: cracked burial mask, collapsed sockets and a crooked seam.
    Fill $graphics $brushes.hide 0 0 40 19
    Fill $graphics $brushes.hideDark 0 0 40 3
    Fill $graphics $brushes.plate 8 3 23 14
    Fill $graphics $brushes.bruise 25 4 6 12
    Fill $graphics $brushes.shadow 5 7 8 5
    Fill $graphics $brushes.shadow 27 8 8 5
    foreach ($segment in @(@(19, 2, 2, 5), @(20, 7, 2, 5), @(18, 12, 2, 6))) {
        Fill $graphics $brushes.seam $segment[0] $segment[1] $segment[2] $segment[3]
    }
    foreach ($stitch in @(@(17, 4, 5), @(18, 8, 6), @(16, 12, 5), @(17, 16, 4))) {
        Fill $graphics $brushes.bone $stitch[0] $stitch[1] $stitch[2] 1
    }
    Fill $graphics $brushes.marrow 17 4 2 1
    Fill $graphics $brushes.sick 8 9 2 2
    Fill $graphics $brushes.shadow 30 9 3 2
    foreach ($point in @(@(3, 4), @(15, 5), @(23, 3), @(36, 5), @(4, 15), @(33, 16))) {
        $bitmap.SetPixel($point[0], $point[1], (Color '#100D12'))
    }

    Fill $graphics $brushes.hideDark 0 22 26 10
    Fill $graphics $brushes.hide 3 22 20 8
    Fill $graphics $brushes.shadow 8 27 10 3
    Fill $graphics $brushes.bruise 4 23 7 5
    Fill $graphics $brushes.seam 12 22 2 6
    Fill $graphics $brushes.seam 14 25 2 3
    Fill $graphics $brushes.bone 4 24 5 1
    Fill $graphics $brushes.bone 18 25 4 1
    Fill $graphics $brushes.marrow 10 28 2 2

    # Ears and ringed marrow tusks; the right tusk is visibly blunted.
    Fill $graphics $brushes.hideDark 42 0 18 6
    Fill $graphics $brushes.bruise 45 1 13 4
    Fill $graphics $brushes.seam 48 2 8 1
    Fill $graphics $brushes.hideDark 62 0 18 6
    Fill $graphics $brushes.plate 64 1 13 4
    Fill $graphics $brushes.shadow 72 2 4 2
    Fill $graphics $brushes.boneDark 28 22 8 9
    Fill $graphics $brushes.bone 29 22 6 8
    Fill $graphics $brushes.marrow 31 23 2 6
    Fill $graphics $brushes.seam 29 25 6 1
    Fill $graphics $brushes.shadow 29 29 6 2
    Fill $graphics $brushes.boneDark 38 22 8 9
    Fill $graphics $brushes.bone 39 22 6 8
    Fill $graphics $brushes.marrow 41 23 2 6
    Fill $graphics $brushes.seam 39 26 6 1
    Fill $graphics $brushes.shadow 42 29 3 2

    # Huge plated torso: mismatched shoulder hides pull against a crooked surgical join.
    Fill $graphics $brushes.hideDark 0 40 72 39
    Fill $graphics $brushes.hide 3 42 66 34
    Fill $graphics $brushes.plate 6 43 24 31
    Fill $graphics $brushes.bruise 42 43 24 31
    Fill $graphics $brushes.hideDark 28 44 7 29
    foreach ($segment in @(@(34, 42, 3, 8), @(36, 50, 3, 8), @(33, 58, 3, 7), @(35, 65, 3, 12))) {
        Fill $graphics $brushes.shadow $segment[0] $segment[1] $segment[2] $segment[3]
        Fill $graphics $brushes.seam ($segment[0] + 1) $segment[1] 1 $segment[3]
    }
    foreach ($stitch in @(@(30, 46, 10), @(32, 52, 11), @(29, 59, 10), @(31, 66, 12), @(33, 72, 8))) {
        Fill $graphics $brushes.bone $stitch[0] $stitch[1] $stitch[2] 1
        $bitmap.SetPixel($stitch[0], $stitch[1], (Color '#D2C197'))
    }
    Fill $graphics $brushes.boneDark 8 45 7 5
    Fill $graphics $brushes.bone 9 45 5 3
    Fill $graphics $brushes.seam 48 45 10 2
    Fill $graphics $brushes.sick 61 66 2 2
    foreach ($point in @(
        @(5, 47), @(12, 52), @(22, 61), @(8, 69), @(26, 72),
        @(47, 50), @(57, 44), @(63, 56), @(50, 69), @(66, 72)
    )) {
        $bitmap.SetPixel($point[0], $point[1], (Color '#100D12'))
    }

    # Raised back ridge is a row of cracked vertebral nodes rather than one clean slab.
    Fill $graphics $brushes.boneDark 74 40 44 24
    Fill $graphics $brushes.bone 76 42 40 19
    Fill $graphics $brushes.marrow 78 43 36 3
    foreach ($node in @(@(79, 43, 6), @(88, 42, 7), @(98, 44, 6), @(107, 42, 8))) {
        Fill $graphics $brushes.shadow $node[0] 46 2 13
        Fill $graphics $brushes.marrow ($node[0] + 2) 47 ($node[2] - 2) 10
        Fill $graphics $brushes.boneDark $node[0] 57 $node[2] 3
    }
    Fill $graphics $brushes.seam 76 60 40 2
    foreach ($point in @(@(82, 50), @(92, 48), @(102, 54), @(111, 49))) {
        $bitmap.SetPixel($point[0], $point[1], (Color '#100D12'))
    }

    # Four weighty legs: dark hooves, folded joints and mismatched repair bands.
    foreach ($leg in @(
        @(0, 82, 20, 18, 'hide'),
        @(22, 82, 20, 18, 'plate'),
        @(44, 82, 24, 17, 'bruise'),
        @(70, 82, 24, 17, 'hide')
    )) {
        Fill $graphics $brushes[$leg[4]] $leg[0] $leg[1] $leg[2] $leg[3]
        Fill $graphics $brushes.hideDark $leg[0] ($leg[1] + $leg[3] - 5) $leg[2] 5
        Fill $graphics $brushes.shadow $leg[0] ($leg[1] + $leg[3] - 2) $leg[2] 2
        Fill $graphics $brushes.seam $leg[0] ($leg[1] + 6) $leg[2] 1
        Fill $graphics $brushes.hideDark ($leg[0] + 2) ($leg[1] + 2) ($leg[2] - 4) 2
    }
    Fill $graphics $brushes.bone 5 84 2 5
    Fill $graphics $brushes.bone 35 87 2 4
    Fill $graphics $brushes.bone 57 83 3 5
    Fill $graphics $brushes.sick 82 87 3 3
    Fill $graphics $brushes.marrow 6 84 1 4
    Fill $graphics $brushes.seam 47 91 18 1
    foreach ($point in @(@(3, 84), @(16, 90), @(27, 85), @(40, 96), @(52, 88), @(65, 94), @(75, 85), @(90, 93))) {
        $bitmap.SetPixel($point[0], $point[1], (Color '#100D12'))
    }

    # Dedicated UV islands for secondary geometry. These match the exact
    # vanilla box unwrap footprints assigned in StitchtuskModel, so no growth,
    # vertebra, tail section, brace or hoof borrows an unrelated body region.
    Paint-HideIsland $graphics $bitmap $brushes 48 20 22 9 'plate' 'bruise'       # left crown growth
    Paint-HideIsland $graphics $bitmap $brushes 72 20 14 6 'bruise' 'plate'       # right crown growth
    Paint-HideIsland $graphics $bitmap $brushes 88 20 16 7 'hide' 'bruise'        # jaw growth
    Paint-HideIsland $graphics $bitmap $brushes 82 0 26 18 'plate' 'hide'          # left shoulder slab
    Paint-HideIsland $graphics $bitmap $brushes 109 0 19 13 'bruise' 'plate'       # right shoulder slab

    Paint-BoneIsland $graphics $bitmap $brushes 0 104 44 23                       # stitched spinal ridge
    Paint-BoneIsland $graphics $bitmap $brushes 106 20 10 7                       # front vertebra
    Paint-BoneIsland $graphics $bitmap $brushes 118 20 9 6                        # middle vertebra
    Paint-BoneIsland $graphics $bitmap $brushes 106 29 10 7                       # rear vertebra

    Paint-HideIsland $graphics $bitmap $brushes 46 104 24 12 'hideDark' 'plate'   # tail root
    Paint-HideIsland $graphics $bitmap $brushes 72 104 16 8 'bruise' 'hide'       # tail knot
    Paint-BoneIsland $graphics $bitmap $brushes 90 104 19 10                      # tail tip

    Paint-HideIsland $graphics $bitmap $brushes 110 104 18 12 'hide' 'plate'      # right foreleg brace
    Paint-HoofIsland $graphics $bitmap $brushes 46 118 26 9                       # right fore hoof
    Paint-HideIsland $graphics $bitmap $brushes 74 116 17 11 'plate' 'bruise'     # left foreleg brace
    Paint-HoofIsland $graphics $bitmap $brushes 102 118 26 9                      # left fore hoof
    Paint-HoofIsland $graphics $bitmap $brushes 74 66 28 9                        # right hind hoof
    Paint-HideIsland $graphics $bitmap $brushes 104 66 19 11 'bruise' 'hide'      # left hindleg brace
    Paint-HoofIsland $graphics $bitmap $brushes 96 82 28 9                        # left hind hoof

    $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
}
finally {
    foreach ($brush in $brushes.Values) {
        $brush.Dispose()
    }
    $graphics.Dispose()
    $bitmap.Dispose()
}

Write-Host "Generated deterministic hard-pixel texture: $output"
