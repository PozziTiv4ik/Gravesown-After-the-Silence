Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot

Add-Type -AssemblyName System.Drawing

function Convert-HexColor {
    param([Parameter(Mandatory = $true)][string]$Hex)
    return [System.Drawing.ColorTranslator]::FromHtml($Hex)
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

$output = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\textures\entity\ribspring.png'
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $output) | Out-Null

$bitmap = New-Object System.Drawing.Bitmap(
    128,
    128,
    [System.Drawing.Imaging.PixelFormat]::Format32bppArgb
)
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.Clear([System.Drawing.Color]::Transparent)
$graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
$graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half

$marrow = New-Object System.Drawing.SolidBrush (Convert-HexColor '#100D12')
$rootDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#231B1D')
$root = New-Object System.Drawing.SolidBrush (Convert-HexColor '#3C2A2C')
$rootLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#594039')
$fiber = New-Object System.Drawing.SolidBrush (Convert-HexColor '#735044')
$boneDark = New-Object System.Drawing.SolidBrush (Convert-HexColor '#756B55')
$bone = New-Object System.Drawing.SolidBrush (Convert-HexColor '#AA9C78')
$boneLight = New-Object System.Drawing.SolidBrush (Convert-HexColor '#D2C296')
$sinew = New-Object System.Drawing.SolidBrush (Convert-HexColor '#A35248')
$lichen = New-Object System.Drawing.SolidBrush (Convert-HexColor '#687743')

try {
    # Model cubes share this compact atlas through vanilla box unwrapping. Keep a
    # fully opaque, hand-pixelled bark-and-sinew underlay so narrow roots, rib
    # edges, hoof caps and fractional-size tail pieces never sample transparent
    # gaps between the larger painted regions. The staggered grain, old sutures
    # and sparse bone splinters keep those fallback faces intentional rather than
    # reading as a flat safety colour.
    Fill-Pixels $graphics $rootDark 0 0 128 128
    for ($grainY = 0; $grainY -lt 128; $grainY += 4) {
        $grainPhase = (([int]($grainY / 4)) % 2) * 3
        for ($grainX = $grainPhase; $grainX -lt 128; $grainX += 8) {
            $shadowWidth = [Math]::Min(3, 128 - $grainX)
            Fill-Pixels $graphics $marrow $grainX $grainY $shadowWidth 1

            $raisedX = $grainX + 4
            if ($raisedX -lt 128) {
                $raisedWidth = [Math]::Min(2, 128 - $raisedX)
                Fill-Pixels $graphics $root $raisedX ($grainY + 1) $raisedWidth 1
            }
        }
    }
    for ($scarY = 11; $scarY -lt 128; $scarY += 20) {
        for ($scarX = 2; $scarX -lt 128; $scarX += 13) {
            $scarWidth = [Math]::Min(5, 128 - $scarX)
            Fill-Pixels $graphics $rootLight $scarX $scarY $scarWidth 1
            if (($scarX + 2) -lt 128 -and ($scarY + 1) -lt 128) {
                $bitmap.SetPixel($scarX + 2, $scarY + 1, (Convert-HexColor '#A35248'))
            }
        }
    }
    foreach ($splinter in @(
        @(5, 17), @(23, 13), @(47, 15), @(12, 35), @(38, 37), @(57, 33),
        @(3, 51), @(29, 55), @(51, 59)
    )) {
        Fill-Pixels $graphics $boneDark $splinter[0] $splinter[1] 2 1
        $bitmap.SetPixel($splinter[0], $splinter[1], (Convert-HexColor '#AA9C78'))
    }

    # Skull assembled from bark and cartilage plates. Pore clusters sit along
    # the seams and never read as a pair of conventional eyes.
    Fill-Pixels $graphics $rootDark 0 0 26 12
    Fill-Pixels $graphics $root 1 1 24 10
    Fill-Pixels $graphics $boneDark 6 0 14 12
    Fill-Pixels $graphics $bone 7 1 12 10
    Fill-Pixels $graphics $boneLight 8 2 9 2
    Fill-Pixels $graphics $rootDark 7 6 4 5
    Fill-Pixels $graphics $marrow 16 6 4 5
    Fill-Pixels $graphics $sinew 11 6 5 2
    Fill-Pixels $graphics $fiber 12 8 3 3
    foreach ($point in @(@(8, 7), @(9, 8), @(17, 7), @(18, 9))) {
        $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#687743'))
    }
    foreach ($point in @(@(3, 2), @(22, 4), @(5, 10), @(21, 9), @(13, 4))) {
        $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#100D12'))
    }
    Fill-Pixels $graphics $rootDark 0 14 14 6
    Fill-Pixels $graphics $root 1 14 12 5
    Fill-Pixels $graphics $boneDark 3 14 8 4
    Fill-Pixels $graphics $bone 4 14 6 3
    Fill-Pixels $graphics $marrow 5 17 4 2
    Fill-Pixels $graphics $fiber 10 16 2 2

    # Crooked root crests are intentionally different: one snapped and one forked.
    Fill-Pixels $graphics $rootDark 28 0 6 13
    Fill-Pixels $graphics $root 29 1 4 10
    Fill-Pixels $graphics $fiber 30 2 2 7
    Fill-Pixels $graphics $bone 30 0 2 3
    Fill-Pixels $graphics $marrow 32 9 2 3
    Fill-Pixels $graphics $lichen 32 6 1 2
    Fill-Pixels $graphics $rootDark 36 0 8 13
    Fill-Pixels $graphics $root 37 1 5 10
    Fill-Pixels $graphics $fiber 38 2 2 7
    Fill-Pixels $graphics $bone 38 0 2 3
    Fill-Pixels $graphics $rootLight 41 3 3 4
    Fill-Pixels $graphics $marrow 42 1 2 3
    Fill-Pixels $graphics $lichen 37 8 2 2

    # Root-wrapped torso with a visible tension seam and rough grain clusters.
    Fill-Pixels $graphics $rootDark 0 22 32 20
    Fill-Pixels $graphics $root 2 23 28 18
    Fill-Pixels $graphics $rootLight 7 23 17 5
    Fill-Pixels $graphics $fiber 9 25 13 2
    Fill-Pixels $graphics $marrow 7 36 18 5
    Fill-Pixels $graphics $sinew 11 29 2 8
    Fill-Pixels $graphics $sinew 19 27 2 10
    Fill-Pixels $graphics $rootDark 13 31 6 2
    Fill-Pixels $graphics $lichen 4 28 3 2
    Fill-Pixels $graphics $lichen 24 33 2 2
    foreach ($point in @(
        @(3, 24), @(15, 23), @(28, 25), @(5, 35), @(27, 39), @(15, 40)
    )) {
        $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#100D12'))
    }

    # Rib plates are painted as three separated arches per flank rather than slabs.
    Fill-Pixels $graphics $boneDark 34 18 14 14
    Fill-Pixels $graphics $marrow 36 20 10 10
    foreach ($x in @(37, 40, 43)) {
        Fill-Pixels $graphics $bone $x 19 2 11
        Fill-Pixels $graphics $boneLight $x 20 1 8
        Fill-Pixels $graphics $marrow $x 24 2 3
    }
    Fill-Pixels $graphics $sinew 35 27 12 2
    Fill-Pixels $graphics $boneDark 48 18 14 14
    Fill-Pixels $graphics $marrow 50 20 10 10
    foreach ($x in @(51, 54, 57)) {
        Fill-Pixels $graphics $bone $x 19 2 11
        Fill-Pixels $graphics $boneLight ($x + 1) 20 1 8
        Fill-Pixels $graphics $marrow $x 23 2 4
    }
    Fill-Pixels $graphics $sinew 49 26 12 2
    Fill-Pixels $graphics $lichen 59 20 2 2

    # Two-segment tail with a worn pale break.
    Fill-Pixels $graphics $rootDark 44 34 14 7
    Fill-Pixels $graphics $root 45 35 8 5
    Fill-Pixels $graphics $fiber 48 36 5 2
    Fill-Pixels $graphics $rootLight 53 36 4 4
    Fill-Pixels $graphics $boneDark 56 36 2 3
    Fill-Pixels $graphics $boneLight 56 36 1 2

    # Springing haunches show bark armor, exposed tendon and bruised joint folds.
    Fill-Pixels $graphics $rootDark 0 44 18 10
    Fill-Pixels $graphics $root 2 45 14 8
    Fill-Pixels $graphics $rootLight 4 45 8 3
    Fill-Pixels $graphics $sinew 6 48 7 2
    Fill-Pixels $graphics $marrow 13 49 3 3
    Fill-Pixels $graphics $rootDark 0 54 18 10
    Fill-Pixels $graphics $root 2 55 14 8
    Fill-Pixels $graphics $fiber 4 56 9 3
    Fill-Pixels $graphics $sinew 6 59 6 2
    Fill-Pixels $graphics $lichen 2 57 2 2
    foreach ($rect in @(
        @(18, 44, 8, 7),
        @(26, 44, 8, 7),
        @(34, 44, 8, 10),
        @(42, 44, 8, 10)
    )) {
        Fill-Pixels $graphics $boneDark $rect[0] $rect[1] $rect[2] $rect[3]
        Fill-Pixels $graphics $bone ($rect[0] + 1) ($rect[1] + 1) ($rect[2] - 2) ($rect[3] - 2)
    }
    Fill-Pixels $graphics $marrow 20 48 4 3
    Fill-Pixels $graphics $marrow 28 48 4 3
    Fill-Pixels $graphics $marrow 36 51 4 3
    Fill-Pixels $graphics $marrow 44 51 4 3
    Fill-Pixels $graphics $sinew 19 46 6 1
    Fill-Pixels $graphics $sinew 27 47 6 1
    Fill-Pixels $graphics $rootDark 34 52 8 2
    Fill-Pixels $graphics $rootDark 42 52 8 2

    # Every secondary cube now owns a non-overlapping island in the right half
    # of the expanded atlas. Paint the complete vanilla box footprint, including
    # its normally easy-to-miss caps, then layer a material-specific grain over
    # it. The model offsets and these rectangles are audited together.
    $detailIslands = @(
        [PSCustomObject]@{ Name = 'head_left_plate';    X = 66;  Y = 0;  W = 12; H = 7; Material = 'bark_bone' },
        [PSCustomObject]@{ Name = 'head_right_plate';   X = 80;  Y = 0;  W = 10; H = 5; Material = 'bark_bone' },
        [PSCustomObject]@{ Name = 'nose_splinter';      X = 92;  Y = 0;  W = 7;  H = 4; Material = 'bone' },
        [PSCustomObject]@{ Name = 'left_root_stem';     X = 101; Y = 0;  W = 6;  H = 8; Material = 'root' },
        [PSCustomObject]@{ Name = 'left_root_fork';     X = 109; Y = 0;  W = 8;  H = 5; Material = 'root' },
        [PSCustomObject]@{ Name = 'left_root_tip';      X = 118; Y = 0;  W = 4;  H = 5; Material = 'bone' },
        [PSCustomObject]@{ Name = 'left_root_prong';    X = 66;  Y = 10; W = 6;  H = 3; Material = 'root' },
        [PSCustomObject]@{ Name = 'right_root_stem';    X = 74;  Y = 9;  W = 6;  H = 8; Material = 'root' },
        [PSCustomObject]@{ Name = 'right_root_fork';    X = 82;  Y = 9;  W = 8;  H = 5; Material = 'root' },
        [PSCustomObject]@{ Name = 'right_root_tip';     X = 92;  Y = 9;  W = 4;  H = 4; Material = 'bone' },
        [PSCustomObject]@{ Name = 'right_root_prong';   X = 98;  Y = 9;  W = 8;  H = 4; Material = 'root' },
        [PSCustomObject]@{ Name = 'throat_fan_upper';   X = 108; Y = 9;  W = 12; H = 6; Material = 'sinew' },
        [PSCustomObject]@{ Name = 'throat_fan_lower';   X = 66;  Y = 18; W = 8;  H = 4; Material = 'sinew' },
        [PSCustomObject]@{ Name = 'body_left_ribs';     X = 76;  Y = 18; W = 14; H = 14; Material = 'bone' },
        [PSCustomObject]@{ Name = 'body_right_ribs';    X = 92;  Y = 18; W = 14; H = 14; Material = 'bone' },
        [PSCustomObject]@{ Name = 'spinal_plate';       X = 108; Y = 18; W = 20; H = 8; Material = 'bark_bone' },
        [PSCustomObject]@{ Name = 'left_rib_upper';     X = 66;  Y = 34; W = 16; H = 9; Material = 'bone' },
        [PSCustomObject]@{ Name = 'left_rib_middle';    X = 84;  Y = 34; W = 14; H = 8; Material = 'bone' },
        [PSCustomObject]@{ Name = 'left_rib_lower';     X = 100; Y = 34; W = 12; H = 7; Material = 'bone' },
        [PSCustomObject]@{ Name = 'right_rib_upper';    X = 66;  Y = 45; W = 15; H = 9; Material = 'bone' },
        [PSCustomObject]@{ Name = 'right_rib_lower';    X = 83;  Y = 45; W = 13; H = 8; Material = 'bone' },
        [PSCustomObject]@{ Name = 'tail_root';          X = 98;  Y = 45; W = 14; H = 7; Material = 'root' },
        [PSCustomObject]@{ Name = 'tail_knot';          X = 114; Y = 45; W = 10; H = 5; Material = 'bark_bone' },
        [PSCustomObject]@{ Name = 'tail_tip';           X = 66;  Y = 56; W = 13; H = 7; Material = 'root' },
        [PSCustomObject]@{ Name = 'right_hind_hoof';    X = 81;  Y = 56; W = 13; H = 5; Material = 'hoof' },
        [PSCustomObject]@{ Name = 'left_hind_hoof';     X = 96;  Y = 56; W = 13; H = 5; Material = 'hoof' },
        [PSCustomObject]@{ Name = 'right_front_hoof';   X = 111; Y = 56; W = 11; H = 5; Material = 'hoof' },
        [PSCustomObject]@{ Name = 'left_leg_growth';    X = 92;  Y = 63; W = 8;  H = 6; Material = 'bark_bone' },
        [PSCustomObject]@{ Name = 'left_front_hoof';    X = 66;  Y = 65; W = 11; H = 5; Material = 'hoof' }
    )

    foreach ($island in $detailIslands) {
        $baseBrush = $rootDark
        $middleBrush = $root
        $highlightBrush = $fiber
        $scarBrush = $lichen

        switch ($island.Material) {
            'bone' {
                $baseBrush = $boneDark
                $middleBrush = $bone
                $highlightBrush = $boneLight
                $scarBrush = $marrow
            }
            'bark_bone' {
                $baseBrush = $rootDark
                $middleBrush = $boneDark
                $highlightBrush = $rootLight
                $scarBrush = $boneLight
            }
            'sinew' {
                $baseBrush = $marrow
                $middleBrush = $sinew
                $highlightBrush = $bone
                $scarBrush = $rootLight
            }
            'hoof' {
                $baseBrush = $marrow
                $middleBrush = $boneDark
                $highlightBrush = $bone
                $scarBrush = $rootDark
            }
        }

        Fill-Pixels $graphics $baseBrush $island.X $island.Y $island.W $island.H
        if ($island.W -gt 2 -and $island.H -gt 2) {
            Fill-Pixels $graphics $middleBrush ($island.X + 1) ($island.Y + 1) ($island.W - 2) ($island.H - 2)
            Fill-Pixels $graphics $highlightBrush ($island.X + 1) ($island.Y + 1) ($island.W - 2) 1
        }
        if ($island.W -gt 4 -and $island.H -gt 3) {
            $scarY = $island.Y + [Math]::Floor($island.H / 2)
            Fill-Pixels $graphics $scarBrush ($island.X + 2) $scarY ($island.W - 4) 1
        }
        for ($grain = 1; $grain -lt ($island.W - 1); $grain += 4) {
            $grainY = $island.Y + 1 + (($grain + $island.Y) % [Math]::Max(1, ($island.H - 2)))
            $bitmap.SetPixel($island.X + $grain, $grainY, (Convert-HexColor '#181719'))
        }
    }

    # Sparse single pixels keep every face hard-edged and hand-pixelled.
    foreach ($point in @(
        @(2, 3), @(22, 4), @(10, 10), @(23, 9), @(29, 5), @(40, 6),
        @(4, 26), @(27, 31), @(6, 38), @(25, 23), @(38, 20), @(60, 27),
        @(3, 47), @(14, 51), @(5, 57), @(15, 61), @(24, 45), @(32, 47),
        @(35, 45), @(47, 48), @(55, 38)
    )) {
        $bitmap.SetPixel($point[0], $point[1], (Convert-HexColor '#181719'))
    }

    $bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
}
finally {
    foreach ($brush in @(
        $marrow, $rootDark, $root, $rootLight, $fiber, $boneDark,
        $bone, $boneLight, $sinew, $lichen
    )) {
        $brush.Dispose()
    }
    $graphics.Dispose()
    $bitmap.Dispose()
}

Write-Host "Created deterministic hard-pixel Ribspring texture: $output"
