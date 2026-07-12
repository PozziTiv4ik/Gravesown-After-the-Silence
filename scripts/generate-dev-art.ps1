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

Save-HollowGrazerTexture
Save-ModIcon
