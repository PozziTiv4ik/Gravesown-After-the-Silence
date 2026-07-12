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

function Save-HollowGrazerTexture {
    $output = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\textures\entity\hollow_grazer.png'
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $output) | Out-Null

    $bitmap = New-PixelBitmap -Width 128 -Height 64
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([System.Drawing.Color]::Transparent)

    $shadow = New-Object System.Drawing.SolidBrush (Convert-HexColor '#171515')
    $soil = New-Object System.Drawing.SolidBrush (Convert-HexColor '#292522')
    $hide = New-Object System.Drawing.SolidBrush (Convert-HexColor '#554943')
    $bruise = New-Object System.Drawing.SolidBrush (Convert-HexColor '#725052')
    $bone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#B6A986')
    $sick = New-Object System.Drawing.SolidBrush (Convert-HexColor '#8A9A4A')
    $wound = New-Object System.Drawing.SolidBrush (Convert-HexColor '#7E292B')

    try {
        # Head UV: blank sealed face with bone seams and one sickly sensory pore.
        Fill-Pixels $graphics $hide 0 0 28 14
        Fill-Pixels $graphics $soil 7 7 7 7
        Fill-Pixels $graphics $shadow 7 12 7 2
        Fill-Pixels $graphics $bone 10 7 1 5
        Fill-Pixels $graphics $bruise 8 8 2 2
        Fill-Pixels $graphics $sick 12 9 1 1
        Fill-Pixels $graphics $shadow 21 7 7 7

        # Hidden jaw: dark flesh rim with irregular old teeth.
        Fill-Pixels $graphics $bruise 0 14 32 11
        Fill-Pixels $graphics $shadow 8 22 8 3
        Fill-Pixels $graphics $wound 9 22 6 1
        foreach ($x in @(9, 11, 14)) {
            Fill-Pixels $graphics $bone $x 23 1 2
        }

        # Asymmetric bone growths.
        Fill-Pixels $graphics $bone 0 28 14 8
        Fill-Pixels $graphics $shadow 2 30 2 3
        Fill-Pixels $graphics $bone 14 28 12 10
        Fill-Pixels $graphics $soil 18 31 2 4

        # Body: soil hide, bruised plates and exposed rib fragments.
        Fill-Pixels $graphics $hide 32 0 44 28
        Fill-Pixels $graphics $soil 32 10 10 18
        Fill-Pixels $graphics $bruise 42 10 12 18
        Fill-Pixels $graphics $shadow 43 24 10 3
        Fill-Pixels $graphics $wound 47 15 3 7
        foreach ($y in @(12, 16, 20)) {
            Fill-Pixels $graphics $bone 42 $y 5 1
        }
        Fill-Pixels $graphics $soil 54 10 10 18
        Fill-Pixels $graphics $hide 64 10 12 18

        # Spine and four deliberately mismatched legs.
        Fill-Pixels $graphics $bone 32 28 28 15
        Fill-Pixels $graphics $shadow 46 40 12 3
        Fill-Pixels $graphics $soil 0 36 16 15
        Fill-Pixels $graphics $hide 16 36 12 14
        Fill-Pixels $graphics $bruise 28 43 12 17
        Fill-Pixels $graphics $soil 40 43 16 17
        Fill-Pixels $graphics $bone 31 55 2 5
        Fill-Pixels $graphics $bone 47 56 3 4

        # Sparse pixels keep the sheet Minecraft-like instead of airbrushed.
        foreach ($point in @(
            @(4, 4), @(17, 6), @(36, 13), @(39, 21), @(57, 4),
            @(67, 16), @(6, 42), @(22, 44), @(35, 51), @(51, 48)
        )) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#171515'))
        }

        $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $shadow.Dispose()
        $soil.Dispose()
        $hide.Dispose()
        $bruise.Dispose()
        $bone.Dispose()
        $sick.Dispose()
        $wound.Dispose()
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
        shadow = New-Object System.Drawing.SolidBrush (Convert-HexColor '#171515')
        soil = New-Object System.Drawing.SolidBrush (Convert-HexColor '#292522')
        hide = New-Object System.Drawing.SolidBrush (Convert-HexColor '#554943')
        bruise = New-Object System.Drawing.SolidBrush (Convert-HexColor '#725052')
        sinew = New-Object System.Drawing.SolidBrush (Convert-HexColor '#9B5548')
        bone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#B6A986')
        pale = New-Object System.Drawing.SolidBrush (Convert-HexColor '#D0C39D')
        sick = New-Object System.Drawing.SolidBrush (Convert-HexColor '#8A9A4A')
        wound = New-Object System.Drawing.SolidBrush (Convert-HexColor '#7E292B')
    }
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
        Fill-Pixels $graphics $p.shadow 3 3 10 11
        Fill-Pixels $graphics $p.hide 4 2 8 10
        Fill-Pixels $graphics $p.bruise 5 4 6 3
        Fill-Pixels $graphics $p.shadow 5 7 6 5
        Fill-Pixels $graphics $p.sinew 7 2 1 5
        Fill-Pixels $graphics $p.bone 8 3 1 3
        Fill-Pixels $graphics $p.sick 11 9 1 2
    }

    Save-ItemTexture 'quietskin_coat' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 3 3 10 12
        Fill-Pixels $graphics $p.hide 4 2 8 12
        Fill-Pixels $graphics $p.bruise 5 4 6 8
        Fill-Pixels $graphics $p.soil 2 4 3 7
        Fill-Pixels $graphics $p.soil 11 4 3 7
        Fill-Pixels $graphics $p.sinew 7 3 1 10
        foreach ($y in @(5, 8, 11)) {
            Fill-Pixels $graphics $p.bone 8 $y 1 1
        }
    }

    Save-ItemTexture 'quietskin_legwraps' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 3 2 10 13
        Fill-Pixels $graphics $p.hide 4 2 8 5
        Fill-Pixels $graphics $p.bruise 4 7 3 7
        Fill-Pixels $graphics $p.soil 9 7 3 7
        foreach ($y in @(8, 11)) {
            Fill-Pixels $graphics $p.sinew 4 $y 3 1
            Fill-Pixels $graphics $p.sinew 9 $y 3 1
        }
        Fill-Pixels $graphics $p.bone 7 3 2 1
    }

    Save-ItemTexture 'quietskin_boots' {
        param($bitmap, $graphics, $p)
        Fill-Pixels $graphics $p.shadow 2 5 5 9
        Fill-Pixels $graphics $p.shadow 9 5 5 9
        Fill-Pixels $graphics $p.hide 3 4 4 8
        Fill-Pixels $graphics $p.hide 9 4 4 8
        Fill-Pixels $graphics $p.soil 2 11 6 3
        Fill-Pixels $graphics $p.soil 8 11 6 3
        Fill-Pixels $graphics $p.sinew 3 7 4 1
        Fill-Pixels $graphics $p.sinew 9 7 4 1
        Fill-Pixels $graphics $p.bone 5 5 1 2
        Fill-Pixels $graphics $p.bone 10 5 1 2
    }
}

function Save-QuietskinArmorTextures {
    $outputDir = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\textures\models\armor'
    New-Item -ItemType Directory -Force -Path $outputDir | Out-Null

    foreach ($layer in @(1, 2)) {
        $output = Join-Path $outputDir "quietskin_layer_$layer.png"
        $bitmap = New-PixelBitmap -Width 64 -Height 32
        $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $p = New-GravesownPalette

        try {
            if ($layer -eq 1) {
                # Hood and outer hood layer.
                Fill-Pixels $graphics $p.hide 0 0 32 16
                Fill-Pixels $graphics $p.soil 32 0 32 16
                Fill-Pixels $graphics $p.bruise 8 3 8 8
                Fill-Pixels $graphics $p.shadow 8 11 8 3
                Fill-Pixels $graphics $p.sinew 16 1 1 14
                Fill-Pixels $graphics $p.bone 17 5 1 4
                Fill-Pixels $graphics $p.sick 44 8 2 2

                # Coat body and arms.
                Fill-Pixels $graphics $p.hide 16 16 24 16
                Fill-Pixels $graphics $p.bruise 20 18 12 12
                Fill-Pixels $graphics $p.soil 40 16 16 16
                Fill-Pixels $graphics $p.sinew 27 17 1 14
                foreach ($y in @(20, 24, 28)) {
                    Fill-Pixels $graphics $p.bone 28 $y 1 1
                }

                # Boots share the standard leg UV region.
                Fill-Pixels $graphics $p.soil 0 16 16 16
                Fill-Pixels $graphics $p.hide 4 17 8 9
                Fill-Pixels $graphics $p.sinew 4 22 8 1
                Fill-Pixels $graphics $p.shadow 0 27 16 5
            }
            else {
                # Inner armor layer used by legwraps.
                Fill-Pixels $graphics $p.hide 0 16 16 16
                Fill-Pixels $graphics $p.bruise 4 17 8 14
                Fill-Pixels $graphics $p.sinew 4 20 8 1
                Fill-Pixels $graphics $p.sinew 4 25 8 1
                Fill-Pixels $graphics $p.soil 16 16 24 16
                Fill-Pixels $graphics $p.shadow 22 25 12 5
                Fill-Pixels $graphics $p.bone 27 18 1 7
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

Save-HollowGrazerTexture
Save-ModIcon
Save-QuietskinItemTextures
Save-QuietskinArmorTextures
Save-FoundationBlockTextures
Save-BootstrapFloraTextures
