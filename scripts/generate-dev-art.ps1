Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot

Add-Type -AssemblyName System.Drawing

function Convert-HexColor {
    param([Parameter(Mandatory = $true)][string]$Hex)
    return [System.Drawing.ColorTranslator]::FromHtml($Hex)
}

function New-PixelBitmap {
    param(
        [Parameter(Mandatory = $true)][int]$Width,
        [Parameter(Mandatory = $true)][int]$Height
    )
    return New-Object System.Drawing.Bitmap(
        $Width,
        $Height,
        [System.Drawing.Imaging.PixelFormat]::Format32bppArgb
    )
}

function Fill-Pixels {
    param(
        [Parameter(Mandatory = $true)][System.Drawing.Graphics]$Graphics,
        [Parameter(Mandatory = $true)][System.Drawing.Brush]$Brush,
        [Parameter(Mandatory = $true)][int]$X,
        [Parameter(Mandatory = $true)][int]$Y,
        [Parameter(Mandatory = $true)][int]$Width,
        [Parameter(Mandatory = $true)][int]$Height
    )
    $Graphics.FillRectangle($Brush, $X, $Y, $Width, $Height)
}

function Clear-Pixels {
    param(
        [Parameter(Mandatory = $true)][System.Drawing.Bitmap]$Bitmap,
        [Parameter(Mandatory = $true)][int]$X,
        [Parameter(Mandatory = $true)][int]$Y,
        [Parameter(Mandatory = $true)][int]$Width,
        [Parameter(Mandatory = $true)][int]$Height
    )
    for ($pixelY = $Y; $pixelY -lt ($Y + $Height); $pixelY++) {
        for ($pixelX = $X; $pixelX -lt ($X + $Width); $pixelX++) {
            $Bitmap.SetPixel($pixelX, $pixelY, [System.Drawing.Color]::Transparent)
        }
    }
}

function Paint-EntityAtlasUnderlay {
    param(
        [Parameter(Mandatory = $true)][System.Drawing.Bitmap]$Bitmap,
        [Parameter(Mandatory = $true)][System.Drawing.Graphics]$Graphics,
        [Parameter(Mandatory = $true)][System.Drawing.Brush]$BaseBrush,
        [Parameter(Mandatory = $true)][System.Drawing.Brush]$PatchBrush,
        [Parameter(Mandatory = $true)][System.Drawing.Color]$AccentColor,
        [Parameter(Mandatory = $true)][int]$Width,
        [Parameter(Mandatory = $true)][int]$Height
    )

    # Model cubes can intentionally share UV origins, and their complete unfolded
    # footprints are larger than the obvious front face. A mottled opaque underlay
    # keeps every side of every added volume visible; the anatomy-specific passes
    # below then replace it with scars, bone, flesh and seams where they matter.
    Fill-Pixels $Graphics $BaseBrush 0 0 $Width $Height
    for ($tileY = 0; $tileY -lt $Height; $tileY += 8) {
        for ($tileX = 0; $tileX -lt $Width; $tileX += 8) {
            $tileIndex = ([int]($tileX / 8) * 5) + ([int]($tileY / 8) * 3)
            if (($tileIndex % 3) -eq 0) {
                Fill-Pixels $Graphics $PatchBrush ($tileX + 1) ($tileY + 2) 4 1
            }
            elseif (($tileIndex % 3) -eq 1) {
                Fill-Pixels $Graphics $PatchBrush ($tileX + 4) ($tileY + 5) 3 2
            }
            else {
                Fill-Pixels $Graphics $PatchBrush $tileX ($tileY + 6) 3 1
            }

            $accentX = $tileX + (($tileIndex + 2) % 7)
            $accentY = $tileY + (($tileIndex * 2 + 1) % 7)
            if ($accentX -lt $Width -and $accentY -lt $Height) {
                $Bitmap.SetPixel($accentX, $accentY, $AccentColor)
            }
        }
    }
}

function Paint-EntityUvIsland {
    param(
        [Parameter(Mandatory = $true)][System.Drawing.Bitmap]$Bitmap,
        [Parameter(Mandatory = $true)][System.Drawing.Graphics]$Graphics,
        [Parameter(Mandatory = $true)][System.Drawing.Brush]$PrimaryBrush,
        [Parameter(Mandatory = $true)][System.Drawing.Brush]$SeamBrush,
        [Parameter(Mandatory = $true)][System.Drawing.Color]$AccentColor,
        [Parameter(Mandatory = $true)][int]$X,
        [Parameter(Mandatory = $true)][int]$Y,
        [Parameter(Mandatory = $true)][int]$Width,
        [Parameter(Mandatory = $true)][int]$Height,
        [Parameter(Mandatory = $true)][int]$Seed
    )

    Fill-Pixels $Graphics $PrimaryBrush $X $Y $Width $Height
    if ($Width -gt 2 -and $Height -gt 2) {
        $seamX = $X + 1 + ($Seed % ($Width - 2))
        $seamY = $Y + 1 + (($Seed * 3) % ($Height - 2))
        Fill-Pixels $Graphics $SeamBrush $seamX $Y 1 $Height
        Fill-Pixels $Graphics $SeamBrush $X $seamY $Width 1
    }
    for ($mark = 0; $mark -lt 3; $mark++) {
        $markX = $X + (($Seed + $mark * 5) % $Width)
        $markY = $Y + (($Seed * 2 + $mark * 3) % $Height)
        $Bitmap.SetPixel($markX, $markY, $AccentColor)
    }
}

function Save-HollowGrazerTexture {
    $output = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\textures\entity\hollow_grazer.png'
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $output) | Out-Null

    $bitmap = New-PixelBitmap -Width 128 -Height 128
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([System.Drawing.Color]::Transparent)

    $void = New-Object System.Drawing.SolidBrush (Convert-HexColor '#100D12')
    $crust = New-Object System.Drawing.SolidBrush (Convert-HexColor '#211C20')
    $hideDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#382A2E')
    $hide = New-Object System.Drawing.SolidBrush (Convert-HexColor '#563B3E')
    $flesh = New-Object System.Drawing.SolidBrush (Convert-HexColor '#7D3D43')
    $sinew = New-Object System.Drawing.SolidBrush (Convert-HexColor '#B35A4C')
    $boneDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#776C55')
    $bone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#B6A780')
    $boneLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#D7C79C')
    $mold = New-Object System.Drawing.SolidBrush (Convert-HexColor '#778745')

    try {
        Paint-EntityAtlasUnderlay $bitmap $graphics $hideDark $crust (Convert-HexColor '#493148') 128 128

        # A split burial-mask skull. Broken plates and pits replace the former
        # featureless face while keeping the creature blind and readable.
        Fill-Pixels $graphics $hideDark 0 0 28 14
        Fill-Pixels $graphics $hide 1 1 26 12
        Fill-Pixels $graphics $boneDark 7 1 7 12
        Fill-Pixels $graphics $bone 8 1 5 11
        Fill-Pixels $graphics $boneLight 9 2 3 3
        Fill-Pixels $graphics $void 14 7 7 7
        Fill-Pixels $graphics $crust 15 8 5 5
        Fill-Pixels $graphics $flesh 21 7 6 7
        Fill-Pixels $graphics $sinew 22 8 1 5
        Fill-Pixels $graphics $void 9 9 3 2
        Fill-Pixels $graphics $mold 10 9 1 1
        foreach ($point in @(@(3, 2), @(5, 5), @(18, 3), @(24, 4), @(4, 11), @(19, 12))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#211C20'))
        }
        foreach ($point in @(@(9, 4), @(11, 6), @(8, 8), @(12, 11))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#776C55'))
        }

        # The jaw is a wet inner cavity with an irregular bone comb and raw hinge.
        Fill-Pixels $graphics $flesh 0 14 32 11
        Fill-Pixels $graphics $hideDark 1 15 30 3
        Fill-Pixels $graphics $void 8 19 16 6
        Fill-Pixels $graphics $sinew 7 18 18 2
        foreach ($x in @(9, 12, 15, 19, 22)) {
            Fill-Pixels $graphics $boneDark $x 19 2 4
            Fill-Pixels $graphics $boneLight $x 19 1 3
        }
        Fill-Pixels $graphics $mold 4 16 2 1
        Fill-Pixels $graphics $crust 26 18 4 3

        # Uneven horn/bone growths, chipped and crusted rather than clean blocks.
        Fill-Pixels $graphics $boneDark 0 28 14 8
        Fill-Pixels $graphics $bone 1 28 12 7
        Fill-Pixels $graphics $boneLight 2 29 8 2
        Fill-Pixels $graphics $void 2 32 3 3
        Fill-Pixels $graphics $crust 9 33 4 2
        Fill-Pixels $graphics $boneDark 14 28 12 10
        Fill-Pixels $graphics $bone 15 28 10 9
        Fill-Pixels $graphics $flesh 18 31 3 5
        Fill-Pixels $graphics $sinew 19 31 1 5
        Fill-Pixels $graphics $mold 23 29 2 2

        # Torso: old hide islands stretched over red tissue, a split belly and
        # exposed rib staples. Small clusters create scale without blur.
        Fill-Pixels $graphics $hideDark 32 0 44 28
        Fill-Pixels $graphics $hide 34 1 40 26
        Fill-Pixels $graphics $crust 32 10 9 18
        Fill-Pixels $graphics $flesh 41 10 15 18
        Fill-Pixels $graphics $sinew 43 11 2 15
        Fill-Pixels $graphics $void 50 13 5 13
        foreach ($y in @(12, 16, 20, 24)) {
            Fill-Pixels $graphics $boneDark 39 $y 9 2
            Fill-Pixels $graphics $bone 40 $y 7 1
        }
        Fill-Pixels $graphics $hideDark 56 10 9 18
        Fill-Pixels $graphics $hide 65 10 11 18
        Fill-Pixels $graphics $mold 67 13 3 2
        Fill-Pixels $graphics $boneDark 70 20 4 5
        foreach ($point in @(
            @(35, 4), @(38, 8), @(45, 3), @(52, 6), @(59, 3), @(69, 6),
            @(36, 17), @(60, 22), @(68, 24), @(73, 12)
        )) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#100D12'))
        }
        foreach ($point in @(@(47, 11), @(48, 18), @(42, 22), @(63, 14), @(71, 11))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#B35A4C'))
        }

        # Cracked spine and four materially different legs sell the stitched
        # ecosystem silhouette even when the mob is standing still.
        Fill-Pixels $graphics $boneDark 32 28 28 15
        Fill-Pixels $graphics $bone 34 29 24 12
        Fill-Pixels $graphics $boneLight 35 30 21 2
        foreach ($x in @(39, 46, 53)) {
            Fill-Pixels $graphics $void $x 33 2 8
            Fill-Pixels $graphics $flesh ($x + 1) 36 2 4
        }
        Fill-Pixels $graphics $hideDark 0 36 16 15
        Fill-Pixels $graphics $crust 1 37 14 13
        Fill-Pixels $graphics $hideDark 16 36 12 14
        Fill-Pixels $graphics $hide 17 37 10 12
        Fill-Pixels $graphics $flesh 28 43 12 17
        Fill-Pixels $graphics $sinew 29 47 10 2
        Fill-Pixels $graphics $crust 40 43 16 17
        Fill-Pixels $graphics $boneDark 41 44 14 5
        Fill-Pixels $graphics $bone 43 45 10 3
        Fill-Pixels $graphics $void 0 48 16 3
        Fill-Pixels $graphics $void 16 47 12 3
        Fill-Pixels $graphics $void 28 57 12 3
        Fill-Pixels $graphics $void 40 57 16 3
        Fill-Pixels $graphics $mold 5 41 2 2
        Fill-Pixels $graphics $boneLight 31 54 2 4
        Fill-Pixels $graphics $boneLight 49 52 3 4

        # Dedicated lower-atlas UV islands for the added volume. Coordinates and
        # extents match HollowGrazerModel's complete unfolded cube footprints.
        Paint-EntityUvIsland $bitmap $graphics $bone $boneDark (Convert-HexColor '#D7C79C') 0 64 14 7 1       # left skull growth
        Paint-EntityUvIsland $bitmap $graphics $boneDark $crust (Convert-HexColor '#B6A780') 14 64 10 8 2    # right skull growth
        Paint-EntityUvIsland $bitmap $graphics $bone $boneDark (Convert-HexColor '#D7C79C') 24 64 16 7 3     # crown plate
        Paint-EntityUvIsland $bitmap $graphics $flesh $hideDark (Convert-HexColor '#B35A4C') 40 64 10 6 4    # cheek tumour
        Paint-EntityUvIsland $bitmap $graphics $bone $boneDark (Convert-HexColor '#D7C79C') 50 64 4 4 5      # left tooth
        Paint-EntityUvIsland $bitmap $graphics $boneDark $void (Convert-HexColor '#D7C79C') 54 64 4 4 6      # right tooth
        Paint-EntityUvIsland $bitmap $graphics $flesh $void (Convert-HexColor '#B35A4C') 58 64 10 4 7         # inner jaw knot
        Paint-EntityUvIsland $bitmap $graphics $hide $crust (Convert-HexColor '#778745') 68 64 24 9 8         # rear hide pad
        Paint-EntityUvIsland $bitmap $graphics $boneDark $void (Convert-HexColor '#D7C79C') 92 64 28 15 9    # exposed spine
        Paint-EntityUvIsland $bitmap $graphics $bone $boneDark (Convert-HexColor '#D7C79C') 0 80 6 5 10      # spine barb one
        Paint-EntityUvIsland $bitmap $graphics $boneDark $void (Convert-HexColor '#B6A780') 6 80 6 4 11      # spine barb two
        Paint-EntityUvIsland $bitmap $graphics $bone $crust (Convert-HexColor '#D7C79C') 12 80 6 4 12        # spine barb three
        Paint-EntityUvIsland $bitmap $graphics $crust $flesh (Convert-HexColor '#B35A4C') 18 80 22 12 13     # right shoulder mass
        Paint-EntityUvIsland $bitmap $graphics $flesh $sinew (Convert-HexColor '#D7C79C') 40 80 10 7 14      # right shoulder spur
        Paint-EntityUvIsland $bitmap $graphics $hide $hideDark (Convert-HexColor '#778745') 50 80 18 10 15  # left shoulder mass
        Paint-EntityUvIsland $bitmap $graphics $boneDark $flesh (Convert-HexColor '#D7C79C') 68 80 8 5 16    # left shoulder hook
        Paint-EntityUvIsland $bitmap $graphics $flesh $hideDark (Convert-HexColor '#B35A4C') 76 80 18 8 17   # tail stump
        Paint-EntityUvIsland $bitmap $graphics $sinew $void (Convert-HexColor '#D7C79C') 94 80 12 6 18       # tail cord
        Paint-EntityUvIsland $bitmap $graphics $crust $flesh (Convert-HexColor '#B35A4C') 106 80 14 9 19     # hind-leg growth
        Paint-EntityUvIsland $bitmap $graphics $hideDark $void (Convert-HexColor '#776C55') 0 92 18 7 20    # right hind hoof
        Paint-EntityUvIsland $bitmap $graphics $hide $crust (Convert-HexColor '#D7C79C') 18 92 16 7 21      # left hind hoof
        Paint-EntityUvIsland $bitmap $graphics $flesh $void (Convert-HexColor '#B6A780') 34 92 16 6 22       # right front hoof
        Paint-EntityUvIsland $bitmap $graphics $boneDark $flesh (Convert-HexColor '#D7C79C') 50 92 14 10 23 # foreleg splint
        Paint-EntityUvIsland $bitmap $graphics $crust $void (Convert-HexColor '#778745') 64 92 18 7 24      # left front hoof

        $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $void.Dispose()
        $crust.Dispose()
        $hideDark.Dispose()
        $hide.Dispose()
        $flesh.Dispose()
        $sinew.Dispose()
        $boneDark.Dispose()
        $bone.Dispose()
        $boneLight.Dispose()
        $mold.Dispose()
        $graphics.Dispose()
        $bitmap.Dispose()
    }

    Write-Host "Created $output"
}

function Save-ModIcon {
    $output = Join-Path $script:ProjectRoot 'src\main\resources\gravesown.png'
    $bitmap = New-PixelBitmap -Width 64 -Height 64
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([System.Drawing.Color]::Transparent)

    $shadow = New-Object System.Drawing.SolidBrush (Convert-HexColor '#171515')
    $soil = New-Object System.Drawing.SolidBrush (Convert-HexColor '#292522')
    $hide = New-Object System.Drawing.SolidBrush (Convert-HexColor '#554943')
    $bone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#B6A986')
    $sick = New-Object System.Drawing.SolidBrush (Convert-HexColor '#8A9A4A')
    $wound = New-Object System.Drawing.SolidBrush (Convert-HexColor '#7E292B')

    try {
        Fill-Pixels $graphics $shadow 4 4 56 56
        Fill-Pixels $graphics $soil 8 8 48 48
        Fill-Pixels $graphics $hide 14 16 36 30
        Fill-Pixels $graphics $bone 18 20 28 20
        Fill-Pixels $graphics $shadow 22 25 7 6
        Fill-Pixels $graphics $shadow 35 25 7 6
        Fill-Pixels $graphics $wound 29 31 6 9
        Fill-Pixels $graphics $soil 20 40 24 7
        foreach ($x in @(22, 27, 32, 37)) {
            Fill-Pixels $graphics $bone $x 40 3 5
        }
        Fill-Pixels $graphics $sick 28 11 8 5
        Fill-Pixels $graphics $sick 31 8 2 5
        Fill-Pixels $graphics $bone 12 13 8 4
        Fill-Pixels $graphics $bone 44 13 8 4

        $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $shadow.Dispose()
        $soil.Dispose()
        $hide.Dispose()
        $bone.Dispose()
        $sick.Dispose()
        $wound.Dispose()
        $graphics.Dispose()
        $bitmap.Dispose()
    }

    Write-Host "Created $output"
}

function New-GravesownPalette {
    return @{
        shadow = New-Object System.Drawing.SolidBrush (Convert-HexColor '#100D12')
        soil = New-Object System.Drawing.SolidBrush (Convert-HexColor '#292522')
        hide = New-Object System.Drawing.SolidBrush (Convert-HexColor '#554943')
        bruise = New-Object System.Drawing.SolidBrush (Convert-HexColor '#725052')
        sinew = New-Object System.Drawing.SolidBrush (Convert-HexColor '#B35A4C')
        bone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#B6A780')
        pale = New-Object System.Drawing.SolidBrush (Convert-HexColor '#D7C79C')
        sick = New-Object System.Drawing.SolidBrush (Convert-HexColor '#7C8F47')
        wound = New-Object System.Drawing.SolidBrush (Convert-HexColor '#7D3D43')
        crust = New-Object System.Drawing.SolidBrush (Convert-HexColor '#211C20')
        hideDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#382A2E')
        flesh = New-Object System.Drawing.SolidBrush (Convert-HexColor '#7D3D43')
        fleshLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#A54E50')
        boneDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#776C55')
        vein = New-Object System.Drawing.SolidBrush (Convert-HexColor '#493148')
        woodDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#24191F')
        wood = New-Object System.Drawing.SolidBrush (Convert-HexColor '#3B2730')
        woodLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#5A3D49')
        stoneDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#29272D')
        stone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#3A373F')
        stoneLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#5C5661')
        membrane = New-Object System.Drawing.SolidBrush (Convert-HexColor '#404C36')
        membraneLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#657052')
        armorShadow = New-Object System.Drawing.SolidBrush (Convert-HexColor '#171319')
        armorDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#2B2228')
        armorHide = New-Object System.Drawing.SolidBrush (Convert-HexColor '#4D343B')
        armorBruise = New-Object System.Drawing.SolidBrush (Convert-HexColor '#713D47')
        armorSinew = New-Object System.Drawing.SolidBrush (Convert-HexColor '#A9594E')
        armorBone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#C0AD7F')
    }
}

function Save-WoundscentTexture {
    $output = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\textures\entity\woundscent.png'
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $output) | Out-Null

    $bitmap = New-PixelBitmap -Width 128 -Height 128
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([System.Drawing.Color]::Transparent)
    $p = New-GravesownPalette

    try {
        Paint-EntityAtlasUnderlay $bitmap $graphics $p.hideDark $p.crust (Convert-HexColor '#493148') 128 128

        # Blind head: stitched eyelid scars, a wet scent cleft and a bone brow
        # produce a readable face without conventional eyes.
        Fill-Pixels $graphics $p.hideDark 0 0 30 14
        Fill-Pixels $graphics $p.hide 1 1 28 12
        Fill-Pixels $graphics $p.crust 7 6 8 7
        Fill-Pixels $graphics $p.flesh 15 6 8 7
        Fill-Pixels $graphics $p.shadow 9 7 12 2
        Fill-Pixels $graphics $p.sinew 10 7 1 5
        Fill-Pixels $graphics $p.sinew 19 7 1 5
        Fill-Pixels $graphics $p.boneDark 11 5 8 2
        Fill-Pixels $graphics $p.bone 12 5 6 1
        Fill-Pixels $graphics $p.sick 22 9 3 2
        Fill-Pixels $graphics $p.vein 3 3 5 1
        foreach ($point in @(@(2, 10), @(5, 12), @(25, 3), @(27, 6), @(14, 3))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#100D12'))
        }

        # Muzzle has porous nostrils and short backward teeth, not a flat black box.
        Fill-Pixels $graphics $p.flesh 0 14 20 8
        Fill-Pixels $graphics $p.fleshLight 2 15 16 4
        Fill-Pixels $graphics $p.shadow 5 17 10 5
        Fill-Pixels $graphics $p.sick 3 16 2 2
        Fill-Pixels $graphics $p.sick 15 16 2 2
        foreach ($x in @(6, 9, 12, 15)) {
            Fill-Pixels $graphics $p.boneDark $x 18 2 3
            $bitmap.SetPixel($x, 18, (Convert-HexColor '#D7C79C'))
        }
        Fill-Pixels $graphics $p.sinew 1 20 18 1

        # Paired scent lobes are veined membranes with luminous tips.
        Fill-Pixels $graphics $p.vein 30 0 6 8
        Fill-Pixels $graphics $p.flesh 31 1 4 6
        Fill-Pixels $graphics $p.sinew 32 2 2 4
        Fill-Pixels $graphics $p.sick 31 5 3 2
        Fill-Pixels $graphics $p.crust 36 0 6 8
        Fill-Pixels $graphics $p.bruise 37 1 4 6
        Fill-Pixels $graphics $p.sinew 38 2 2 4
        Fill-Pixels $graphics $p.sick 39 5 2 2
        Fill-Pixels $graphics $p.shadow 30 7 12 1

        # Torso alternates mud-crusted hide and exposed muscle around a stitched
        # central incision. Bone staples cross instead of forming clean stripes.
        Fill-Pixels $graphics $p.crust 0 24 38 24
        Fill-Pixels $graphics $p.hideDark 2 25 34 22
        Fill-Pixels $graphics $p.hide 4 26 13 20
        Fill-Pixels $graphics $p.flesh 17 27 12 20
        Fill-Pixels $graphics $p.shadow 28 26 8 20
        Fill-Pixels $graphics $p.sinew 18 29 2 17
        Fill-Pixels $graphics $p.fleshLight 21 29 3 15
        foreach ($y in @(30, 34, 39, 44)) {
            Fill-Pixels $graphics $p.boneDark 14 $y 10 2
            Fill-Pixels $graphics $p.bone 15 $y 8 1
        }
        Fill-Pixels $graphics $p.sick 25 35 3 2
        Fill-Pixels $graphics $p.vein 7 31 7 1
        Fill-Pixels $graphics $p.vein 5 38 9 1
        foreach ($point in @(@(3, 28), @(10, 27), @(33, 31), @(7, 44), @(31, 43))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#100D12'))
        }

        # Raised vertebrae are dirty and cracked; the tail ends as a sinew cord.
        Fill-Pixels $graphics $p.boneDark 38 16 26 14
        Fill-Pixels $graphics $p.bone 39 17 24 12
        Fill-Pixels $graphics $p.pale 40 18 22 2
        foreach ($x in @(45, 51, 58)) {
            Fill-Pixels $graphics $p.shadow $x 19 2 9
            Fill-Pixels $graphics $p.flesh ($x + 1) 23 2 5
        }
        Fill-Pixels $graphics $p.sick 60 18 2 2
        Fill-Pixels $graphics $p.hideDark 48 36 16 8
        Fill-Pixels $graphics $p.flesh 49 37 10 5
        Fill-Pixels $graphics $p.sinew 52 38 11 2
        Fill-Pixels $graphics $p.shadow 59 40 5 4

        # Long forelimbs look freshly flayed; hind limbs carry old grave crust.
        Fill-Pixels $graphics $p.flesh 0 48 12 14
        Fill-Pixels $graphics $p.fleshLight 2 49 8 9
        Fill-Pixels $graphics $p.hideDark 12 48 12 14
        Fill-Pixels $graphics $p.hide 14 49 8 9
        Fill-Pixels $graphics $p.crust 24 48 12 12
        Fill-Pixels $graphics $p.boneDark 26 49 8 7
        Fill-Pixels $graphics $p.shadow 36 48 12 12
        Fill-Pixels $graphics $p.hideDark 38 49 8 7
        foreach ($x in @(2, 7, 14, 19)) {
            Fill-Pixels $graphics $p.sinew $x 53 2 1
        }
        Fill-Pixels $graphics $p.bone 27 50 2 5
        Fill-Pixels $graphics $p.bone 42 51 2 5
        Fill-Pixels $graphics $p.sick 32 54 2 2
        Fill-Pixels $graphics $p.shadow 0 59 24 3
        Fill-Pixels $graphics $p.shadow 24 57 24 3

        # Dedicated lower-atlas UV islands for every added sensory, body and limb
        # volume. Their rectangles mirror WoundscentModel's full cube footprints.
        Paint-EntityUvIsland $bitmap $graphics $p.boneDark $p.shadow (Convert-HexColor '#D7C79C') 0 64 24 5 31    # blind brow
        Paint-EntityUvIsland $bitmap $graphics $p.crust $p.flesh (Convert-HexColor '#7C8F47') 24 64 12 6 32       # left face cyst
        Paint-EntityUvIsland $bitmap $graphics $p.flesh $p.vein (Convert-HexColor '#B35A4C') 36 64 13 8 33        # right face cyst
        Paint-EntityUvIsland $bitmap $graphics $p.bone $p.shadow (Convert-HexColor '#D7C79C') 49 64 4 4 34        # left tooth
        Paint-EntityUvIsland $bitmap $graphics $p.boneDark $p.shadow (Convert-HexColor '#D7C79C') 53 64 4 4 35    # right tooth
        Paint-EntityUvIsland $bitmap $graphics $p.vein $p.sinew (Convert-HexColor '#7C8F47') 57 64 6 7 36          # left scent stem
        Paint-EntityUvIsland $bitmap $graphics $p.flesh $p.vein (Convert-HexColor '#7C8F47') 63 64 8 5 37          # left scent bulb
        Paint-EntityUvIsland $bitmap $graphics $p.sinew $p.vein (Convert-HexColor '#7C8F47') 71 64 4 6 38          # left scent tip
        Paint-EntityUvIsland $bitmap $graphics $p.sick $p.vein (Convert-HexColor '#D7C79C') 75 64 6 3 39           # left feeler
        Paint-EntityUvIsland $bitmap $graphics $p.crust $p.sinew (Convert-HexColor '#7C8F47') 81 64 6 7 40         # right scent stem
        Paint-EntityUvIsland $bitmap $graphics $p.bruise $p.vein (Convert-HexColor '#7C8F47') 87 64 7 6 41         # right scent bulb
        Paint-EntityUvIsland $bitmap $graphics $p.vein $p.sinew (Convert-HexColor '#7C8F47') 94 64 4 5 42          # right scent tip
        Paint-EntityUvIsland $bitmap $graphics $p.sick $p.shadow (Convert-HexColor '#B35A4C') 98 64 6 3 43         # right feeler
        Paint-EntityUvIsland $bitmap $graphics $p.boneDark $p.shadow (Convert-HexColor '#D7C79C') 0 73 26 14 44   # buried back ridge
        Paint-EntityUvIsland $bitmap $graphics $p.crust $p.flesh (Convert-HexColor '#B35A4C') 26 73 20 14 45      # right torso lobe
        Paint-EntityUvIsland $bitmap $graphics $p.hide $p.vein (Convert-HexColor '#7C8F47') 46 73 15 10 46         # left torso lobe
        Paint-EntityUvIsland $bitmap $graphics $p.bone $p.shadow (Convert-HexColor '#B35A4C') 61 73 26 14 47       # raised vertebrae
        Paint-EntityUvIsland $bitmap $graphics $p.boneDark $p.flesh (Convert-HexColor '#D7C79C') 87 73 6 4 48     # spine barb one
        Paint-EntityUvIsland $bitmap $graphics $p.bone $p.shadow (Convert-HexColor '#7C8F47') 93 73 6 4 49         # spine barb two
        Paint-EntityUvIsland $bitmap $graphics $p.boneDark $p.vein (Convert-HexColor '#D7C79C') 99 73 6 4 50      # spine barb three
        Paint-EntityUvIsland $bitmap $graphics $p.flesh $p.shadow (Convert-HexColor '#B35A4C') 105 73 16 8 51     # tail base
        Paint-EntityUvIsland $bitmap $graphics $p.crust $p.flesh (Convert-HexColor '#7C8F47') 0 88 14 7 52        # tail knot
        Paint-EntityUvIsland $bitmap $graphics $p.sinew $p.vein (Convert-HexColor '#D7C79C') 14 88 15 8 53        # tail cord
        Paint-EntityUvIsland $bitmap $graphics $p.hideDark $p.shadow (Convert-HexColor '#776C55') 29 88 16 6 54  # right hind foot
        Paint-EntityUvIsland $bitmap $graphics $p.boneDark $p.flesh (Convert-HexColor '#D7C79C') 45 88 11 8 55    # hind-leg spur
        Paint-EntityUvIsland $bitmap $graphics $p.crust $p.shadow (Convert-HexColor '#7C8F47') 56 88 16 6 56      # left hind foot
        Paint-EntityUvIsland $bitmap $graphics $p.flesh $p.sinew (Convert-HexColor '#D7C79C') 72 88 12 9 57       # foreleg wound
        Paint-EntityUvIsland $bitmap $graphics $p.hide $p.shadow (Convert-HexColor '#B35A4C') 84 88 17 6 58       # right front foot
        Paint-EntityUvIsland $bitmap $graphics $p.bruise $p.shadow (Convert-HexColor '#7C8F47') 101 88 17 6 59   # left front foot

        $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        foreach ($brush in $p.Values) {
            $brush.Dispose()
        }
        $graphics.Dispose()
        $bitmap.Dispose()
    }

    Write-Host "Created $output"
}

function Save-ItemTexture {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][scriptblock]$Painter
    )

    $output = Join-Path $script:ProjectRoot "src\main\resources\assets\gravesown\textures\item\$Name.png"
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $output) | Out-Null

    $bitmap = New-PixelBitmap -Width 16 -Height 16
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([System.Drawing.Color]::Transparent)
    $palette = New-GravesownPalette

    try {
        & $Painter $bitmap $graphics $palette
        $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        foreach ($brush in $palette.Values) {
            $brush.Dispose()
        }
        $graphics.Dispose()
        $bitmap.Dispose()
    }

    Write-Host "Created $output"
}

function Save-QuietskinItemTextures {
    Save-ItemTexture 'ragged_grazer_hide' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 2 3 11 11
        Fill-Pixels $graphics $p.hide 3 2 10 11
        Fill-Pixels $graphics $p.bruise 4 4 8 6
        Fill-Pixels $graphics $p.soil 3 10 3 3
        Fill-Pixels $graphics $p.hide 10 10 3 4
        foreach ($y in @(4, 7, 10)) {
            Fill-Pixels $graphics $p.bone 7 $y 1 2
            Fill-Pixels $graphics $p.sinew 8 ($y + 1) 1 1
        }
    }

    Save-ItemTexture 'taut_sinew' {
        param($bitmap, $graphics, $p)
        for ($i = 0; $i -lt 10; $i++) {
            $x = 3 + $i
            $y = 12 - $i
            Fill-Pixels $graphics $p.shadow $x $y 2 2
            Fill-Pixels $graphics $p.sinew $x ($y - 1) 1 2
            if (($i % 3) -eq 0) {
                $bitmap.SetPixel($x + 1, $y, (Convert-HexColor '#B6A986'))
            }
        }
    }

    Save-ItemTexture 'grave_tallow' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 3 7 10 6
        Fill-Pixels $graphics $p.bone 4 5 8 7
        Fill-Pixels $graphics $p.pale 6 4 5 6
        Fill-Pixels $graphics $p.sick 5 9 2 2
        Fill-Pixels $graphics $p.bruise 10 7 2 3
    }

    Save-ItemTexture 'tainted_grazer_meat' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 2 5 12 8
        Fill-Pixels $graphics $p.wound 3 4 10 8
        Fill-Pixels $graphics $p.bruise 5 5 7 6
        Fill-Pixels $graphics $p.sick 4 9 2 2
        Fill-Pixels $graphics $p.bone 10 6 2 4
        Fill-Pixels $graphics $p.pale 11 7 1 2
    }

    Save-ItemTexture 'hollow_jaw' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 2 4 3 8
        Fill-Pixels $graphics $p.shadow 11 4 3 8
        Fill-Pixels $graphics $p.shadow 4 11 8 3
        Fill-Pixels $graphics $p.bone 3 3 3 8
        Fill-Pixels $graphics $p.bone 10 3 3 8
        Fill-Pixels $graphics $p.bone 5 10 6 3
        foreach ($x in @(5, 7, 9, 11)) {
            Fill-Pixels $graphics $p.pale $x 8 1 3
        }
        Fill-Pixels $graphics $p.wound 7 11 2 1
    }

    Save-ItemTexture 'quietskin_hood' {
        param($bitmap, $graphics, $p)
        # Open-faced hood: a hard outer silhouette, transparent face window,
        # bone brow and mismatched hanging cheek guards.
        Fill-Pixels $graphics $p.armorShadow 3 2 10 12
        Fill-Pixels $graphics $p.armorDark 4 1 8 13
        Fill-Pixels $graphics $p.armorHide 5 2 6 11
        Fill-Pixels $graphics $p.armorBruise 5 2 6 3
        Clear-Pixels $bitmap 6 5 5 6
        Fill-Pixels $graphics $p.armorShadow 5 5 1 7
        Fill-Pixels $graphics $p.armorShadow 11 5 1 5
        Fill-Pixels $graphics $p.armorBone 6 4 5 1
        Fill-Pixels $graphics $p.pale 7 4 2 1
        Fill-Pixels $graphics $p.armorSinew 4 4 1 8
        Fill-Pixels $graphics $p.armorSinew 11 8 1 4
        Fill-Pixels $graphics $p.armorHide 5 11 2 3
        Fill-Pixels $graphics $p.armorBruise 10 11 2 3
        Fill-Pixels $graphics $p.armorBone 6 12 2 1
        $bitmap.SetPixel(11, 6, (Convert-HexColor '#7C8F47'))
    }

    Save-ItemTexture 'quietskin_coat' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.armorShadow 2 3 12 12
        Fill-Pixels $graphics $p.armorDark 2 4 4 9
        Fill-Pixels $graphics $p.armorDark 10 4 4 9
        Fill-Pixels $graphics $p.armorHide 4 2 8 13
        Fill-Pixels $graphics $p.armorBruise 5 3 3 10
        Fill-Pixels $graphics $p.armorDark 8 3 3 11
        Fill-Pixels $graphics $p.armorShadow 7 4 2 10
        Fill-Pixels $graphics $p.armorSinew 6 4 1 9
        Fill-Pixels $graphics $p.armorSinew 10 6 1 7
        foreach ($y in @(5, 8, 11)) {
            Fill-Pixels $graphics $p.armorBone 7 $y 3 1
            $bitmap.SetPixel(8, $y, (Convert-HexColor '#D7C79C'))
        }
        Fill-Pixels $graphics $p.armorBone 4 3 2 1
        Fill-Pixels $graphics $p.sick 5 10 1 2
        Fill-Pixels $graphics $p.armorShadow 2 12 4 3
        Fill-Pixels $graphics $p.armorShadow 10 12 4 3
        $bitmap.SetPixel(5, 6, (Convert-HexColor '#171319'))
        $bitmap.SetPixel(10, 4, (Convert-HexColor '#C0AD7F'))
    }

    Save-ItemTexture 'quietskin_legwraps' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.armorShadow 3 2 10 13
        Fill-Pixels $graphics $p.armorDark 4 2 8 5
        Fill-Pixels $graphics $p.armorHide 5 3 6 3
        Fill-Pixels $graphics $p.armorBruise 4 6 4 8
        Fill-Pixels $graphics $p.armorDark 9 6 3 8
        Fill-Pixels $graphics $p.armorShadow 7 6 2 9
        Fill-Pixels $graphics $p.armorSinew 4 5 8 1
        Fill-Pixels $graphics $p.armorSinew 4 8 4 1
        Fill-Pixels $graphics $p.armorSinew 4 11 4 1
        Fill-Pixels $graphics $p.armorSinew 9 9 3 1
        Fill-Pixels $graphics $p.armorSinew 9 12 3 1
        Fill-Pixels $graphics $p.armorBone 6 3 4 1
        Fill-Pixels $graphics $p.pale 7 4 2 1
        Fill-Pixels $graphics $p.armorBone 10 7 1 3
        Fill-Pixels $graphics $p.armorShadow 3 13 5 2
        Fill-Pixels $graphics $p.armorShadow 9 13 4 2
        $bitmap.SetPixel(5, 7, (Convert-HexColor '#171319'))
    }

    Save-ItemTexture 'quietskin_boots' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.armorShadow 2 5 6 10
        Fill-Pixels $graphics $p.armorShadow 8 5 6 10
        Fill-Pixels $graphics $p.armorHide 3 3 4 8
        Fill-Pixels $graphics $p.armorBruise 9 4 4 7
        Fill-Pixels $graphics $p.armorDark 2 10 6 5
        Fill-Pixels $graphics $p.armorDark 8 10 6 5
        Fill-Pixels $graphics $p.armorSinew 3 7 4 1
        Fill-Pixels $graphics $p.armorSinew 9 8 4 1
        Fill-Pixels $graphics $p.armorBone 5 4 1 3
        Fill-Pixels $graphics $p.armorBone 10 5 1 3
        Fill-Pixels $graphics $p.pale 3 12 4 1
        Fill-Pixels $graphics $p.pale 10 12 3 1
        Fill-Pixels $graphics $p.armorShadow 3 14 5 1
        Fill-Pixels $graphics $p.armorShadow 9 14 5 1
        $bitmap.SetPixel(12, 5, (Convert-HexColor '#7C8F47'))
    }
}

function Save-QuietskinArmorTextures {
    $outputDir = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\textures\models\armor'
    New-Item -ItemType Directory -Force -Path $outputDir | Out-Null

    foreach ($layer in @(1, 2)) {
        $output = Join-Path $outputDir "quietskin_layer_$layer.png"
        $bitmap = New-PixelBitmap -Width 128 -Height 128
        $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $p = New-GravesownPalette

        try {
            if ($layer -eq 1) {
                # HOOD. These rectangles are complete unfolded cube footprints
                # owned by QuietskinArmorModel. The face is open because the model
                # has no front plate below the narrow bone brow.
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorDark (Convert-HexColor '#A9594E') 0 0 40 12 101
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorShadow (Convert-HexColor '#4D343B') 40 0 24 10 102
                Paint-EntityUvIsland $bitmap $graphics $p.armorBruise $p.armorDark (Convert-HexColor '#C0AD7F') 64 0 18 15 103
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorShadow (Convert-HexColor '#A9594E') 82 0 18 14 104
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.boneDark (Convert-HexColor '#D7C79C') 100 0 18 2 105
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorDark (Convert-HexColor '#D7C79C') 100 2 20 10 106
                Paint-EntityUvIsland $bitmap $graphics $p.armorBruise $p.armorShadow (Convert-HexColor '#C0AD7F') 120 0 8 5 107
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorShadow (Convert-HexColor '#7C8F47') 120 5 8 4 108

                # COAT. Overlapping hide slabs, oblique harness, bone closures,
                # back splint and two deliberately mismatched shoulders/arms.
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorDark (Convert-HexColor '#A9594E') 0 16 10 12 201
                Paint-EntityUvIsland $bitmap $graphics $p.armorBruise $p.armorShadow (Convert-HexColor '#C0AD7F') 10 16 12 11 202
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorHide (Convert-HexColor '#A9594E') 22 16 20 13 203
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorShadow (Convert-HexColor '#7C8F47') 42 16 10 14 204
                Paint-EntityUvIsland $bitmap $graphics $p.armorBruise $p.armorDark (Convert-HexColor '#C0AD7F') 52 16 10 15 205
                Paint-EntityUvIsland $bitmap $graphics $p.armorSinew $p.armorShadow (Convert-HexColor '#D7C79C') 62 16 20 2 206
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorSinew (Convert-HexColor '#C0AD7F') 62 18 20 3 207
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.boneDark (Convert-HexColor '#D7C79C') 82 16 10 2 208
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#A9594E') 92 16 8 2 209
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorBone (Convert-HexColor '#7C8F47') 100 16 10 6 210
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#D7C79C') 110 16 6 7 211
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorHide (Convert-HexColor '#C0AD7F') 0 32 22 10 212
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#A9594E') 22 32 16 8 213
                Paint-EntityUvIsland $bitmap $graphics $p.armorSinew $p.armorDark (Convert-HexColor '#C0AD7F') 38 32 20 7 214
                Paint-EntityUvIsland $bitmap $graphics $p.armorBruise $p.armorShadow (Convert-HexColor '#7C8F47') 58 32 20 10 215
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorDark (Convert-HexColor '#A9594E') 78 32 20 8 216
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#D7C79C') 98 32 14 10 217
                Paint-EntityUvIsland $bitmap $graphics $p.armorSinew $p.armorDark (Convert-HexColor '#C0AD7F') 0 44 20 7 218
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#D7C79C') 20 44 6 4 219
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorSinew (Convert-HexColor '#C0AD7F') 26 44 6 7 220

                # BOOTS. Raised shin shells, broad scavenged toe caps, ankle
                # bindings and an asymmetric bone splint/buckle.
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorHide (Convert-HexColor '#A9594E') 0 68 20 11 401
                Paint-EntityUvIsland $bitmap $graphics $p.armorShadow $p.armorDark (Convert-HexColor '#C0AD7F') 20 68 24 10 402
                Paint-EntityUvIsland $bitmap $graphics $p.armorSinew $p.armorDark (Convert-HexColor '#D7C79C') 44 68 24 8 403
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#D7C79C') 68 68 6 7 404
                Paint-EntityUvIsland $bitmap $graphics $p.armorBruise $p.armorDark (Convert-HexColor '#7C8F47') 74 68 20 10 405
                Paint-EntityUvIsland $bitmap $graphics $p.armorShadow $p.armorHide (Convert-HexColor '#C0AD7F') 94 68 24 9 406
                Paint-EntityUvIsland $bitmap $graphics $p.armorSinew $p.armorDark (Convert-HexColor '#D7C79C') 0 80 20 7 407
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#7C8F47') 20 80 6 3 408
            }
            else {
                # LEGWRAPS. A thick waist harness, hanging hip patches, separated
                # thigh plates, knee shells and uneven binding bands.
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorSinew (Convert-HexColor '#C0AD7F') 32 44 28 8 301
                Paint-EntityUvIsland $bitmap $graphics $p.armorBruise $p.armorShadow (Convert-HexColor '#D7C79C') 60 44 8 6 302
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorDark (Convert-HexColor '#7C8F47') 68 44 6 5 303
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorShadow (Convert-HexColor '#A9594E') 74 44 12 7 304
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorBruise (Convert-HexColor '#C0AD7F') 86 44 12 12 305
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#D7C79C') 98 44 14 5 306
                Paint-EntityUvIsland $bitmap $graphics $p.armorSinew $p.armorDark (Convert-HexColor '#C0AD7F') 0 56 20 6 307
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorSinew (Convert-HexColor '#7C8F47') 20 56 20 6 308
                Paint-EntityUvIsland $bitmap $graphics $p.armorBruise $p.armorShadow (Convert-HexColor '#A9594E') 40 56 12 6 309
                Paint-EntityUvIsland $bitmap $graphics $p.armorHide $p.armorDark (Convert-HexColor '#C0AD7F') 52 56 12 11 310
                Paint-EntityUvIsland $bitmap $graphics $p.armorBone $p.armorShadow (Convert-HexColor '#D7C79C') 64 56 14 4 311
                Paint-EntityUvIsland $bitmap $graphics $p.armorSinew $p.armorDark (Convert-HexColor '#D7C79C') 78 56 20 6 312
                Paint-EntityUvIsland $bitmap $graphics $p.armorDark $p.armorSinew (Convert-HexColor '#7C8F47') 98 56 20 6 313
            }

            $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
        }
        finally {
            foreach ($brush in $p.Values) {
                $brush.Dispose()
            }
            $graphics.Dispose()
            $bitmap.Dispose()
        }

        Write-Host "Created $output"
    }
}

function New-FoundationPalette {
    return @{
        void = New-Object System.Drawing.SolidBrush (Convert-HexColor '#121116')
        loamDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#211A1B')
        loam = New-Object System.Drawing.SolidBrush (Convert-HexColor '#302526')
        loamLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#463334')
        ashDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#252721')
        ash = New-Object System.Drawing.SolidBrush (Convert-HexColor '#34372E')
        ashLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#46493B')
        lichen = New-Object System.Drawing.SolidBrush (Convert-HexColor '#626748')
        stoneDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#29272D')
        stone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#3A373F')
        stoneLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#4D4851')
        deep = New-Object System.Drawing.SolidBrush (Convert-HexColor '#211F27')
        deepLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#34313C')
        vein = New-Object System.Drawing.SolidBrush (Convert-HexColor '#5A3B46')
        bone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#8F8368')
        rust = New-Object System.Drawing.SolidBrush (Convert-HexColor '#572D33')
        wound = New-Object System.Drawing.SolidBrush (Convert-HexColor '#7E292B')
        woodDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#24191F')
        wood = New-Object System.Drawing.SolidBrush (Convert-HexColor '#3B2730')
        woodLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#5A3D49')
        membraneDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#283024')
        membrane = New-Object System.Drawing.SolidBrush (Convert-HexColor '#404C36')
        membraneLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#657052')
        glowDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#746B54')
        glow = New-Object System.Drawing.SolidBrush (Convert-HexColor '#B6AA81')
        glowLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#D7CAA0')
    }
}

function Save-BlockTexture {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][scriptblock]$Painter
    )

    $output = Join-Path $script:ProjectRoot "src\main\resources\assets\gravesown\textures\block\$Name.png"
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $output) | Out-Null
    $bitmap = New-PixelBitmap -Width 16 -Height 16
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $palette = New-FoundationPalette

    try {
        & $Painter $bitmap $graphics $palette
        $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        foreach ($brush in $palette.Values) {
            $brush.Dispose()
        }
        $graphics.Dispose()
        $bitmap.Dispose()
    }

    Write-Host "Created $output"
}

function Save-FoundationBlockTextures {
    Save-BlockTexture 'grave_loam' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.loam 0 0 16 16
        foreach ($patch in @(
            @(1, 2, 3, 2), @(9, 1, 4, 2), @(5, 7, 4, 2),
            @(12, 10, 3, 3), @(1, 13, 4, 2), @(7, 14, 2, 2)
        )) {
            Fill-Pixels $graphics $p.loamDark $patch[0] $patch[1] $patch[2] $patch[3]
        }
        foreach ($patch in @(
            @(5, 3, 2, 2), @(13, 4, 2, 2), @(2, 8, 3, 2), @(8, 11, 3, 2)
        )) {
            Fill-Pixels $graphics $p.loamLight $patch[0] $patch[1] $patch[2] $patch[3]
        }
        foreach ($point in @(@(4, 12), @(10, 5), @(15, 8))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#8F8368'))
        }
    }

    Save-BlockTexture 'ashen_sod_top' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.ash 0 0 16 16
        foreach ($patch in @(
            @(0, 3, 4, 2), @(7, 1, 3, 3), @(12, 5, 4, 2),
            @(3, 9, 4, 3), @(10, 12, 4, 3), @(0, 14, 2, 2)
        )) {
            Fill-Pixels $graphics $p.ashDark $patch[0] $patch[1] $patch[2] $patch[3]
        }
        foreach ($patch in @(
            @(3, 1, 2, 1), @(10, 4, 2, 2), @(1, 7, 3, 1), @(7, 9, 2, 2), @(14, 14, 2, 1)
        )) {
            Fill-Pixels $graphics $p.ashLight $patch[0] $patch[1] $patch[2] $patch[3]
        }
        foreach ($point in @(@(5, 5), @(6, 5), @(13, 9), @(2, 12), @(9, 15))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#626748'))
        }
        Fill-Pixels $graphics $p.rust 11 8 1 2
    }

    Save-BlockTexture 'ashen_sod_side' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.loam 0 0 16 16
        Fill-Pixels $graphics $p.ash 0 0 16 4
        Fill-Pixels $graphics $p.ashLight 2 1 4 1
        Fill-Pixels $graphics $p.ashDark 8 2 5 2
        Fill-Pixels $graphics $p.lichen 14 0 2 1
        foreach ($root in @(
            @(2, 3, 1, 6), @(6, 3, 1, 3), @(10, 3, 1, 8), @(14, 3, 1, 5)
        )) {
            Fill-Pixels $graphics $p.loamLight $root[0] $root[1] $root[2] $root[3]
        }
        Fill-Pixels $graphics $p.loamDark 0 9 4 3
        Fill-Pixels $graphics $p.loamDark 7 12 4 2
        Fill-Pixels $graphics $p.loamLight 12 10 3 2
        foreach ($point in @(@(3, 6), @(9, 9), @(15, 13))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#8F8368'))
        }
    }

    Save-BlockTexture 'hushstone' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.stone 0 0 16 16
        foreach ($patch in @(
            @(0, 2, 4, 2), @(7, 0, 3, 3), @(12, 4, 4, 3),
            @(3, 8, 4, 2), @(9, 11, 5, 3), @(0, 14, 3, 2)
        )) {
            Fill-Pixels $graphics $p.stoneDark $patch[0] $patch[1] $patch[2] $patch[3]
        }
        foreach ($patch in @(
            @(4, 3, 3, 1), @(10, 2, 2, 2), @(1, 6, 2, 1), @(7, 7, 3, 2), @(4, 13, 3, 1)
        )) {
            Fill-Pixels $graphics $p.stoneLight $patch[0] $patch[1] $patch[2] $patch[3]
        }
        foreach ($point in @(@(5, 9), @(6, 10), @(7, 11), @(14, 8), @(15, 8))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#5A3B46'))
        }
    }

    Save-BlockTexture 'deep_hushstone' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.deep 0 0 16 16
        Fill-Pixels $graphics $p.void 0 3 5 2
        Fill-Pixels $graphics $p.void 10 0 4 3
        Fill-Pixels $graphics $p.void 6 10 6 3
        Fill-Pixels $graphics $p.void 13 14 3 2
        Fill-Pixels $graphics $p.deepLight 4 0 3 2
        Fill-Pixels $graphics $p.deepLight 8 4 5 2
        Fill-Pixels $graphics $p.deepLight 1 8 4 3
        Fill-Pixels $graphics $p.deepLight 3 14 5 2
        foreach ($point in @(
            @(1, 1), @(2, 2), @(3, 2), @(7, 5), @(7, 6),
            @(8, 7), @(9, 7), @(12, 10), @(13, 11), @(14, 12)
        )) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#5A3B46'))
        }
        Fill-Pixels $graphics $p.stoneLight 15 6 1 2
    }

    Save-BlockTexture 'gravebed' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.void 0 0 16 16
        Fill-Pixels $graphics $p.deep 0 1 16 3
        Fill-Pixels $graphics $p.stoneDark 0 6 16 3
        Fill-Pixels $graphics $p.deep 0 11 16 4
        Fill-Pixels $graphics $p.bone 1 3 5 1
        Fill-Pixels $graphics $p.bone 9 7 6 1
        Fill-Pixels $graphics $p.bone 3 12 7 1
        Fill-Pixels $graphics $p.rust 6 2 5 2
        Fill-Pixels $graphics $p.rust 0 8 4 2
        Fill-Pixels $graphics $p.rust 11 13 5 2
        Fill-Pixels $graphics $p.stoneLight 13 1 3 2
        Fill-Pixels $graphics $p.stoneLight 5 7 2 2
        foreach ($point in @(@(0, 4), @(7, 4), @(15, 4), @(2, 10), @(8, 10), @(14, 10))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#121116'))
        }
    }

    Save-BlockTexture 'rootfelt' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.woodDark 0 0 16 16
        Fill-Pixels $graphics $p.wood 1 1 14 14
        foreach ($root in @(
            @(0, 2, 8, 2), @(6, 5, 10, 2), @(0, 9, 11, 2), @(8, 13, 8, 2)
        )) {
            Fill-Pixels $graphics $p.woodDark $root[0] $root[1] $root[2] $root[3]
        }
        Fill-Pixels $graphics $p.woodLight 2 3 5 1
        Fill-Pixels $graphics $p.woodLight 9 7 5 1
        Fill-Pixels $graphics $p.membrane 2 12 5 2
        Fill-Pixels $graphics $p.bone 12 3 2 1
        foreach ($point in @(@(4, 6), @(5, 6), @(11, 11), @(13, 12))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#572D33'))
        }
    }

    Save-BlockTexture 'fibrous_loam' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.loamDark 0 0 16 16
        Fill-Pixels $graphics $p.loam 1 1 14 14
        foreach ($patch in @(
            @(0, 4, 5, 3), @(9, 1, 6, 3), @(5, 9, 8, 3), @(0, 14, 6, 2)
        )) {
            Fill-Pixels $graphics $p.loamDark $patch[0] $patch[1] $patch[2] $patch[3]
        }
        foreach ($fiber in @(
            @(1, 3, 7, 1), @(7, 6, 8, 1), @(2, 11, 6, 1), @(9, 14, 6, 1)
        )) {
            Fill-Pixels $graphics $p.woodLight $fiber[0] $fiber[1] $fiber[2] $fiber[3]
        }
        Fill-Pixels $graphics $p.rust 5 6 1 4
        Fill-Pixels $graphics $p.bone 13 9 2 1
    }

    Save-BlockTexture 'scar_shale' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.stoneDark 0 0 16 16
        foreach ($plate in @(
            @(0, 1, 7, 3), @(9, 0, 7, 4), @(2, 6, 9, 3),
            @(11, 6, 5, 4), @(0, 11, 6, 4), @(7, 12, 9, 4)
        )) {
            Fill-Pixels $graphics $p.deepLight $plate[0] $plate[1] $plate[2] $plate[3]
        }
        foreach ($point in @(
            @(1, 5), @(2, 5), @(6, 4), @(7, 5), @(10, 4), @(11, 5),
            @(5, 10), @(6, 10), @(12, 11), @(13, 10), @(14, 10)
        )) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#572D33'))
        }
        Fill-Pixels $graphics $p.stoneLight 3 7 3 1
        Fill-Pixels $graphics $p.bone 13 2 2 1
    }

    Save-BlockTexture 'marrowstone' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.bone 0 0 16 16
        foreach ($mass in @(
            @(1, 1, 5, 4), @(9, 0, 6, 5), @(4, 7, 8, 4),
            @(0, 12, 5, 4), @(11, 12, 5, 4)
        )) {
            Fill-Pixels $graphics $p.glow $mass[0] $mass[1] $mass[2] $mass[3]
        }
        Fill-Pixels $graphics $p.glowLight 2 2 3 2
        Fill-Pixels $graphics $p.glowLight 7 8 4 2
        foreach ($cavity in @(
            @(6, 1, 2, 3), @(12, 5, 3, 2), @(1, 7, 3, 3), @(7, 12, 3, 3)
        )) {
            Fill-Pixels $graphics $p.rust $cavity[0] $cavity[1] $cavity[2] $cavity[3]
        }
        Fill-Pixels $graphics $p.void 7 2 1 2
        Fill-Pixels $graphics $p.void 2 8 1 2
        Fill-Pixels $graphics $p.stoneDark 13 13 2 2
    }

    Save-BlockTexture 'suture_silt' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.void 0 0 16 16
        Fill-Pixels $graphics $p.loamDark 1 1 14 14
        foreach ($pool in @(
            @(0, 2, 6, 3), @(8, 1, 7, 4), @(3, 7, 9, 4),
            @(0, 12, 5, 4), @(10, 12, 6, 4)
        )) {
            Fill-Pixels $graphics $p.deep $pool[0] $pool[1] $pool[2] $pool[3]
        }
        foreach ($point in @(@(2, 3), @(5, 4), @(10, 2), @(4, 9), @(8, 8), @(13, 13))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#404C36'))
        }
        Fill-Pixels $graphics $p.bone 1 11 5 1
        Fill-Pixels $graphics $p.rust 6 10 1 3
        Fill-Pixels $graphics $p.bone 7 13 6 1
    }

    Save-BlockTexture 'dried_ichor' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.void 0 0 16 16
        Fill-Pixels $graphics $p.rust 1 1 14 14
        Fill-Pixels $graphics $p.loamDark 3 2 5 4
        Fill-Pixels $graphics $p.loamDark 10 1 5 6
        Fill-Pixels $graphics $p.loamDark 1 9 6 6
        Fill-Pixels $graphics $p.loamDark 9 10 6 5
        foreach ($crack in @(
            @(7, 0), @(7, 1), @(8, 2), @(8, 3), @(7, 4), @(6, 5),
            @(7, 8), @(8, 9), @(8, 10), @(7, 11), @(6, 12), @(6, 13),
            @(0, 7), @(1, 7), @(2, 8), @(3, 8), @(12, 7), @(13, 8), @(15, 8)
        )) {
            $bitmap.SetPixel($crack[0], $crack[1], (Convert-HexColor '#121116'))
        }
        Fill-Pixels $graphics $p.wound 3 3 3 1
        Fill-Pixels $graphics $p.wound 10 12 3 1
        Fill-Pixels $graphics $p.bone 12 4 1 1
    }
}

function Save-BootstrapFloraTextures {
    Save-BlockTexture 'ribroot_stem' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.wood 0 0 16 16
        Fill-Pixels $graphics $p.woodDark 0 0 2 16
        Fill-Pixels $graphics $p.woodDark 6 0 2 16
        Fill-Pixels $graphics $p.woodDark 12 0 1 16
        Fill-Pixels $graphics $p.woodLight 3 0 2 16
        Fill-Pixels $graphics $p.woodLight 9 0 2 16
        Fill-Pixels $graphics $p.woodDark 14 0 2 16
        Fill-Pixels $graphics $p.wood 1 3 2 4
        Fill-Pixels $graphics $p.wood 6 9 3 3
        Fill-Pixels $graphics $p.wood 12 5 2 5
        Fill-Pixels $graphics $p.bone 4 6 1 3
        Fill-Pixels $graphics $p.bone 10 12 1 2
        $bitmap.SetPixel(8, 2, (Convert-HexColor '#572D33'))
        $bitmap.SetPixel(13, 13, (Convert-HexColor '#572D33'))
    }

    Save-BlockTexture 'ribroot_stem_top' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.woodDark 0 0 16 16
        Fill-Pixels $graphics $p.wood 1 1 14 14
        Fill-Pixels $graphics $p.woodLight 3 3 10 10
        Fill-Pixels $graphics $p.woodDark 4 4 8 8
        Fill-Pixels $graphics $p.wood 5 5 6 6
        Fill-Pixels $graphics $p.woodLight 6 6 4 4
        Fill-Pixels $graphics $p.rust 7 6 2 5
        Fill-Pixels $graphics $p.bone 8 7 1 3
        Fill-Pixels $graphics $p.woodDark 1 7 3 2
        Fill-Pixels $graphics $p.woodDark 12 5 3 2
        Fill-Pixels $graphics $p.wood 7 0 2 3
        Fill-Pixels $graphics $p.wood 6 13 3 3
    }

    Save-BlockTexture 'ribroot_planks' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.wood 0 0 16 16
        foreach ($y in @(3, 7, 11, 15)) {
            Fill-Pixels $graphics $p.woodDark 0 $y 16 1
        }
        Fill-Pixels $graphics $p.woodLight 1 1 6 1
        Fill-Pixels $graphics $p.woodLight 10 5 5 1
        Fill-Pixels $graphics $p.woodLight 3 9 7 1
        Fill-Pixels $graphics $p.woodLight 8 13 6 1
        Fill-Pixels $graphics $p.woodDark 7 0 1 4
        Fill-Pixels $graphics $p.woodDark 2 8 1 4
        Fill-Pixels $graphics $p.woodDark 12 12 1 4
        Fill-Pixels $graphics $p.rust 12 1 2 1
        Fill-Pixels $graphics $p.rust 5 5 2 1
        Fill-Pixels $graphics $p.bone 9 13 1 1
    }

    Save-BlockTexture 'veil_foliage' {
        param($bitmap, $graphics, $p)
        $graphics.Clear([System.Drawing.Color]::Transparent)
        foreach ($cluster in @(
            @(0, 0, 6, 5), @(8, 0, 7, 4), @(3, 4, 10, 7),
            @(0, 9, 6, 6), @(7, 10, 9, 6), @(13, 5, 3, 5)
        )) {
            Fill-Pixels $graphics $p.membraneDark $cluster[0] $cluster[1] $cluster[2] $cluster[3]
        }
        foreach ($cluster in @(
            @(1, 1, 4, 3), @(9, 1, 5, 2), @(4, 5, 8, 5),
            @(1, 10, 4, 4), @(8, 11, 7, 4), @(14, 6, 2, 3)
        )) {
            Fill-Pixels $graphics $p.membrane $cluster[0] $cluster[1] $cluster[2] $cluster[3]
        }
        foreach ($point in @(
            @(2, 1), @(10, 2), @(5, 6), @(9, 5), @(2, 12),
            @(9, 13), @(13, 12), @(15, 7)
        )) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#657052'))
        }
        foreach ($point in @(@(4, 2), @(11, 0), @(6, 8), @(3, 11), @(12, 14))) {
            $bitmap.SetPixel($point[0], $point[1], [System.Drawing.Color]::Transparent)
        }
        Fill-Pixels $graphics $p.rust 7 7 2 1
    }

    Save-BlockTexture 'threadgrass' {
        param($bitmap, $graphics, $p)
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Fill-Pixels $graphics $p.membraneDark 2 14 12 2
        Fill-Pixels $graphics $p.membrane 4 9 2 6
        Fill-Pixels $graphics $p.membrane 8 6 2 9
        Fill-Pixels $graphics $p.membraneDark 12 8 1 7
        Fill-Pixels $graphics $p.membraneDark 2 10 1 5
        foreach ($point in @(
            @(3, 9), @(3, 8), @(4, 7), @(5, 8), @(6, 7),
            @(7, 6), @(8, 5), @(9, 4), @(10, 5), @(11, 6),
            @(12, 7), @(13, 8), @(6, 10), @(7, 9), @(10, 9), @(11, 8)
        )) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#657052'))
        }
        Fill-Pixels $graphics $p.bone 7 13 1 2
    }

    Save-BlockTexture 'ribroot_shoot' {
        param($bitmap, $graphics, $p)
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Fill-Pixels $graphics $p.woodDark 6 5 4 11
        Fill-Pixels $graphics $p.wood 7 4 2 11
        Fill-Pixels $graphics $p.woodLight 8 6 1 7
        Fill-Pixels $graphics $p.woodDark 3 8 4 2
        Fill-Pixels $graphics $p.woodDark 9 6 4 2
        Fill-Pixels $graphics $p.membraneDark 1 5 5 4
        Fill-Pixels $graphics $p.membrane 2 4 4 3
        Fill-Pixels $graphics $p.membraneDark 10 2 5 5
        Fill-Pixels $graphics $p.membrane 10 3 4 3
        Fill-Pixels $graphics $p.membraneDark 5 1 5 4
        Fill-Pixels $graphics $p.membraneLight 6 1 3 2
        Fill-Pixels $graphics $p.bone 7 10 1 2
        Fill-Pixels $graphics $p.woodDark 4 15 8 1
    }

    Save-BlockTexture 'pallid_bulb' {
        param($bitmap, $graphics, $p)
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Fill-Pixels $graphics $p.membraneDark 7 8 2 8
        Fill-Pixels $graphics $p.membrane 8 9 1 6
        Fill-Pixels $graphics $p.glowDark 4 4 8 7
        Fill-Pixels $graphics $p.glow 5 3 6 8
        Fill-Pixels $graphics $p.glowLight 6 4 4 5
        Fill-Pixels $graphics $p.bone 7 3 2 2
        Fill-Pixels $graphics $p.rust 6 9 4 2
        Fill-Pixels $graphics $p.membraneDark 3 12 4 2
        Fill-Pixels $graphics $p.membraneDark 9 11 4 2
        Fill-Pixels $graphics $p.membrane 2 11 3 1
        Fill-Pixels $graphics $p.membrane 11 10 3 1
        Fill-Pixels $graphics $p.woodDark 5 15 6 1
    }
}

function Save-FirstToolItemTextures {
    Save-ItemTexture 'ribroot_splint' {
        param($bitmap, $graphics, $p)
        for ($i = 0; $i -lt 10; $i++) {
            $x = 3 + $i
            $y = 13 - $i
            Fill-Pixels $graphics $p.shadow $x $y 2 2
            Fill-Pixels $graphics $p.wood $x $y 1 2
            if (($i % 3) -eq 1) {
                $bitmap.SetPixel($x + 1, $y, (Convert-HexColor '#5A3D49'))
            }
        }
        Fill-Pixels $graphics $p.bone 12 3 2 1
        Fill-Pixels $graphics $p.woodDark 2 13 3 2
    }

    Save-ItemTexture 'thread_binding' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 3 4 10 9
        Fill-Pixels $graphics $p.membrane 4 3 8 9
        Fill-Pixels $graphics $p.shadow 6 5 5 5
        Fill-Pixels $graphics $p.membraneLight 4 5 2 5
        Fill-Pixels $graphics $p.membraneLight 7 3 4 1
        Fill-Pixels $graphics $p.membrane 7 11 5 2
        Fill-Pixels $graphics $p.sinew 10 8 3 1
        Fill-Pixels $graphics $p.bone 6 10 2 1
    }

    Save-ItemTexture 'hushstone_shard' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 3 10 10 4
        Fill-Pixels $graphics $p.stoneDark 4 7 8 6
        Fill-Pixels $graphics $p.stone 6 4 5 8
        Fill-Pixels $graphics $p.stoneLight 8 3 2 7
        Fill-Pixels $graphics $p.stoneLight 5 9 2 3
        Fill-Pixels $graphics $p.wound 9 7 2 3
        Fill-Pixels $graphics $p.bone 7 11 2 1
        $bitmap.SetPixel(10, 5, (Convert-HexColor '#171515'))
    }

    Save-ItemTexture 'crude_handpick' {
        param($bitmap, $graphics, $p)
        for ($i = 0; $i -lt 8; $i++) {
            $x = 3 + $i
            $y = 13 - $i
            Fill-Pixels $graphics $p.shadow $x $y 3 3
            Fill-Pixels $graphics $p.wood $x ($y + 1) 2 2
            $bitmap.SetPixel($x + 1, $y + 1, (Convert-HexColor '#5A3D49'))
        }
        Fill-Pixels $graphics $p.shadow 4 3 10 4
        Fill-Pixels $graphics $p.woodDark 5 4 9 2
        Fill-Pixels $graphics $p.wood 6 4 5 1
        Fill-Pixels $graphics $p.bone 4 4 2 2
        Fill-Pixels $graphics $p.membrane 8 5 3 3
        Fill-Pixels $graphics $p.membraneLight 9 5 1 3
    }

    Save-ItemTexture 'bound_knife' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 2 11 6 4
        Fill-Pixels $graphics $p.woodDark 3 11 5 3
        Fill-Pixels $graphics $p.wood 4 10 4 3
        Fill-Pixels $graphics $p.membrane 6 9 3 3
        Fill-Pixels $graphics $p.membraneLight 7 9 1 3
        foreach ($segment in @(
            @(7, 8, 4, 3), @(9, 6, 4, 3), @(11, 4, 3, 3), @(12, 2, 2, 3)
        )) {
            Fill-Pixels $graphics $p.shadow $segment[0] $segment[1] $segment[2] $segment[3]
        }
        Fill-Pixels $graphics $p.stone 8 8 3 2
        Fill-Pixels $graphics $p.stone 10 6 3 2
        Fill-Pixels $graphics $p.stoneLight 11 5 2 2
        Fill-Pixels $graphics $p.stoneLight 12 3 2 2
        Fill-Pixels $graphics $p.wound 9 8 1 1
        Fill-Pixels $graphics $p.bone 4 12 1 1
    }
}

Save-HollowGrazerTexture
Save-WoundscentTexture
Save-ModIcon
Save-QuietskinItemTextures
Save-QuietskinArmorTextures
Save-FoundationBlockTextures
Save-BootstrapFloraTextures
Save-FirstToolItemTextures
