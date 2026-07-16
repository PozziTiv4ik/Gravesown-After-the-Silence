[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

# Art Language V3 is the final, deterministic owner of shipped raster art. Earlier
# family generators remain useful source constructors. World, item and entity art
# keeps its authored natural material colours; the cold navy presentation palette is
# deliberately limited to GUI and launcher assets.

function Convert-Color([string]$Hex) {
    [System.Drawing.ColorTranslator]::FromHtml($Hex)
}

function Get-ArtHash([int]$X, [int]$Y, [int]$Seed) {
    [int64]$value = ([int64]$X * 73856093) -bxor ([int64]$Y * 19349663) -bxor ([int64]$Seed * 83492791)
    $value = ($value -bxor ($value -shr 13)) % 2147483647
    $value = (($value * 48271) + 12345) % 2147483647
    if ($value -lt 0) { $value = -$value }
    [int]$value
}

function New-ArgbBitmap([int]$Width = 16, [int]$Height = 16) {
    [System.Drawing.Bitmap]::new(
        $Width,
        $Height,
        [System.Drawing.Imaging.PixelFormat]::Format32bppArgb
    )
}

function Fill-Rect(
    [System.Drawing.Graphics]$Graphics,
    [string]$Hex,
    [int]$X,
    [int]$Y,
    [int]$Width,
    [int]$Height
) {
    $brush = [System.Drawing.SolidBrush]::new((Convert-Color $Hex))
    try { $Graphics.FillRectangle($brush, $X, $Y, $Width, $Height) }
    finally { $brush.Dispose() }
}

function Set-SafePixel(
    [System.Drawing.Bitmap]$Bitmap,
    [int]$X,
    [int]$Y,
    [System.Drawing.Color]$Color
) {
    if ($X -ge 0 -and $X -lt $Bitmap.Width -and $Y -ge 0 -and $Y -lt $Bitmap.Height) {
        $Bitmap.SetPixel($X, $Y, $Color)
    }
}

$script:DirectPaths = [System.Collections.Generic.HashSet[string]]::new(
    [System.StringComparer]::OrdinalIgnoreCase
)
$script:DirectTreatments = @{}

# Large photographic screen backgrounds predate the deterministic pixel-art
# pipeline.  generate-presentation-background.ps1 always rebuilds the launcher
# master from an immutable source before this pass.  Mark the two copies direct
# so the generic GUI matrix below cannot apply a second, cumulative grade.
$backgroundMaster = Join-Path $script:ProjectRoot 'launcher\assets\launcher_background.png'
if (Test-Path -LiteralPath $backgroundMaster) {
    foreach ($relativeTarget in @(
        'src\main\resources\assets\gravesown\textures\gui\screen_background.png',
        'src\main\resources\assets\gravesown\textures\gui\title_background.png'
    )) {
        $target = Join-Path $script:ProjectRoot $relativeTarget
        New-Item -ItemType Directory -Force -Path (Split-Path -Parent $target) | Out-Null
        Copy-Item -LiteralPath $backgroundMaster -Destination $target -Force
        $full = [System.IO.Path]::GetFullPath($target)
        [void]$script:DirectPaths.Add($full)
        $script:DirectTreatments[$full] = 'direct-cold-navy-presentation-background'
    }
}

function Save-DirectTexture(
    [System.Drawing.Bitmap]$Bitmap,
    [string]$RelativePath,
    [string]$Treatment
) {
    $path = Join-Path $script:ProjectRoot $RelativePath
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
    $Bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $full = [System.IO.Path]::GetFullPath($path)
    [void]$script:DirectPaths.Add($full)
    $script:DirectTreatments[$full] = $Treatment
}

function New-Texture(
    [string]$Name,
    [scriptblock]$Painter,
    [string]$Treatment = 'direct-v3',
    [bool]$Transparent = $false
) {
    $bitmap = New-ArgbBitmap
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        if ($Transparent) { $graphics.Clear([System.Drawing.Color]::Transparent) }
        & $Painter $bitmap $graphics
        Save-DirectTexture $bitmap "src\main\resources\assets\gravesown\textures\block\$Name.png" $Treatment
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Paint-Cluster(
    [System.Drawing.Bitmap]$Bitmap,
    [int]$X,
    [int]$Y,
    [System.Drawing.Color]$Color,
    [int]$Variant
) {
    $shapes = @(
        @(@(0,0), @(1,0), @(0,1)),
        @(@(0,0), @(1,0), @(2,0), @(1,1)),
        @(@(0,0), @(0,1), @(1,1), @(1,2)),
        @(@(0,0), @(1,0), @(1,1), @(2,1)),
        @(@(0,0), @(1,0), @(0,1), @(1,1))
    )
    foreach ($point in $shapes[$Variant % $shapes.Count]) {
        Set-SafePixel $Bitmap ($X + $point[0]) ($Y + $point[1]) $Color
    }
}

function New-NaturalMaterial(
    [string]$Name,
    [string[]]$Palette,
    [int]$Seed,
    [ValidateSet('soil','stone','sand','organic')][string]$Grammar
) {
    $colors = @($Palette | ForEach-Object { Convert-Color $_ })
    if ($colors.Count -ne 5) { throw "$Name requires exactly five V3 colours." }

    New-Texture $Name {
        param($bitmap, $graphics)
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                $hash = Get-ArtHash $x $y $Seed
                $index = if (($hash % 19) -eq 0) { 0 }
                    elseif (($hash % 7) -eq 0) { 1 }
                    elseif (($hash % 11) -eq 0) { 3 }
                    else { 2 }
                $bitmap.SetPixel($x, $y, $colors[$index])
            }
        }

        # Compact value islands echo vanilla stone/dirt noise without a shared
        # brick grid or the long diagonal crack that dominated V2 hillsides.
        for ($index = 0; $index -lt 9; $index++) {
            $hash = Get-ArtHash ($index + 3) ($Seed + $index * 5) ($Seed + 101)
            $x = $hash % 14
            $y = ($hash -shr 6) % 14
            $tone = if (($index % 4) -eq 0) { 1 } elseif (($index % 3) -eq 0) { 3 } else { 2 }
            Paint-Cluster $bitmap $x $y $colors[$tone] (($hash -shr 11) % 5)
        }

        switch ($Grammar) {
            'soil' {
                foreach ($point in @(@(1,4),@(4,12),@(8,6),@(12,2),@(14,10))) {
                    Set-SafePixel $bitmap $point[0] $point[1] $colors[4]
                }
                Set-SafePixel $bitmap 5 9 $colors[0]
                Set-SafePixel $bitmap 6 9 $colors[0]
            }
            'stone' {
                $paths = if (($Seed % 2) -eq 0) {
                    @(@(@(2,5),@(3,5),@(4,6),@(5,6)), @(@(11,12),@(11,11),@(12,10)))
                }
                else {
                    @(@(@(4,2),@(4,3),@(5,4)), @(@(13,8),@(12,8),@(11,9),@(10,9)))
                }
                foreach ($path in $paths) {
                    foreach ($point in $path) { Set-SafePixel $bitmap $point[0] $point[1] $colors[0] }
                }
                Set-SafePixel $bitmap 5 5 $colors[3]
            }
            'sand' {
                foreach ($point in @(@(1,7),@(3,2),@(6,13),@(9,5),@(12,11),@(14,3))) {
                    Set-SafePixel $bitmap $point[0] $point[1] $colors[4]
                }
            }
            'organic' {
                foreach ($path in @(
                    @(@(3,1),@(3,2),@(4,3),@(4,4)),
                    @(@(12,10),@(11,11),@(11,12),@(10,13))
                )) {
                    foreach ($point in $path) { Set-SafePixel $bitmap $point[0] $point[1] $colors[4] }
                }
            }
        }
    } 'direct-natural-v3'
}

# Daylight-readable terrain.  Saturation lives in local material families instead
# of a global purple/gray filter; ashen_sod is the natural-readability reference.
New-NaturalMaterial 'grave_loam'     @('#4C392A','#634A34','#7C6044','#977757','#B29268') 111 'soil'
New-NaturalMaterial 'ashen_sod_top'  @('#3D5B31','#54763E','#6F944E','#88AA61','#AD9A59') 117 'organic'
New-NaturalMaterial 'hushstone'      @('#44484A','#5A6061','#747A79','#929792','#A99E85') 123 'stone'
New-NaturalMaterial 'deep_hushstone' @('#293033','#394246','#4B565A','#657074','#78695B') 129 'stone'
New-NaturalMaterial 'gravebed'       @('#1D2325','#2A3235','#384246','#505B5E','#6D5D4D') 131 'stone'
New-NaturalMaterial 'rootfelt'       @('#30462A','#476038','#607A49','#7B965D','#9A8055') 137 'organic'
New-NaturalMaterial 'fibrous_loam'   @('#513B28','#6B4E33','#866542','#A17F57','#BEA071') 141 'soil'
New-NaturalMaterial 'scar_shale'     @('#57352B','#704638','#8B5A46','#A87358','#C08D69') 143 'stone'
New-NaturalMaterial 'marrowstone'    @('#776E59','#91866B','#ADA080','#C7BB98','#DED3AE') 147 'stone'
New-NaturalMaterial 'suture_silt'    @('#34432E','#4B5A3B','#65734A','#7F8D5E','#9A8150') 153 'soil'
New-NaturalMaterial 'dried_ichor'    @('#5B3024','#783F2B','#98543A','#B76E49','#D28C5B') 159 'organic'
New-NaturalMaterial 'abyssal_silt'   @('#203530','#315047','#42685A','#5C826E','#87734D') 161 'soil'
New-NaturalMaterial 'brinebone'      @('#7B735F','#958B70','#B0A482','#CBBE9A','#E1D5B1') 167 'stone'
New-NaturalMaterial 'gloam_muck'     @('#263A2F','#39533F','#4F6D50','#698969','#8B7850') 171 'soil'
New-NaturalMaterial 'gloam_sand'     @('#786C4D','#96835C','#B39D70','#CDB989','#E1CFA0') 173 'sand'

# Biome-expansion stones stay inside the same natural value ladder while each
# keeps one readable geological cue.  They deliberately avoid the old regular
# masonry courses: these are raw world materials, not manufactured bricks.
New-Texture 'veined_shale' {
    param($bitmap, $graphics)
    $palette = @('#303537','#42494A','#586061','#707777','#989071') | ForEach-Object { Convert-Color $_ }
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $hash = Get-ArtHash $x $y 191
            $tone = if (($hash % 17) -eq 0) { 0 } elseif (($hash % 7) -eq 0) { 1 } elseif (($hash % 11) -eq 0) { 3 } else { 2 }
            $bitmap.SetPixel($x, $y, $palette[$tone])
        }
    }
    foreach ($point in @(@(0,11),@(1,11),@(2,10),@(3,10),@(4,9),@(5,9),@(6,8),@(7,8),@(8,8),@(9,7),@(10,7),@(11,6),@(12,6),@(13,5),@(14,5),@(15,4))) {
        Set-SafePixel $bitmap $point[0] $point[1] $palette[4]
    }
    foreach ($point in @(@(2,2),@(3,2),@(8,13),@(9,13),@(12,10))) { Set-SafePixel $bitmap $point[0] $point[1] $palette[0] }
} 'direct-geology-veined-v3'

New-Texture 'splintered_marrowstone' {
    param($bitmap, $graphics)
    $palette = @('#665F50','#817764','#A0957C','#BDB092','#DED2AD') | ForEach-Object { Convert-Color $_ }
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $hash = Get-ArtHash $x $y 193
            $tone = if (($hash % 13) -eq 0) { 1 } elseif (($hash % 9) -eq 0) { 3 } else { 2 }
            $bitmap.SetPixel($x, $y, $palette[$tone])
        }
    }
    foreach ($path in @(
        @(@(3,0),@(3,1),@(4,2),@(4,3),@(5,4),@(5,5),@(6,6)),
        @(@(13,8),@(12,8),@(11,9),@(10,9),@(10,10),@(9,11),@(9,12),@(8,13),@(8,15))
    )) {
        foreach ($point in $path) { Set-SafePixel $bitmap $point[0] $point[1] $palette[0] }
    }
    foreach ($point in @(@(2,7),@(7,2),@(12,13),@(14,3))) { Set-SafePixel $bitmap $point[0] $point[1] $palette[4] }
} 'direct-geology-splintered-v3'

New-Texture 'cairnstone' {
    param($bitmap, $graphics)
    $palette = @('#3C403B','#50564E','#687067','#858C7E','#AAA990') | ForEach-Object { Convert-Color $_ }
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $hash = Get-ArtHash $x $y 197
            $tone = if (($hash % 19) -eq 0) { 0 } elseif (($hash % 6) -eq 0) { 1 } elseif (($hash % 10) -eq 0) { 3 } else { 2 }
            $bitmap.SetPixel($x, $y, $palette[$tone])
        }
    }
    foreach ($cluster in @(@(1,2,4),@(9,1,3),@(4,9,5),@(11,12,4))) {
        for ($x = $cluster[0]; $x -lt ($cluster[0] + $cluster[2]); $x++) {
            Set-SafePixel $bitmap $x $cluster[1] $palette[3]
        }
    }
    foreach ($point in @(@(2,13),@(3,13),@(8,5),@(9,5),@(14,7))) { Set-SafePixel $bitmap $point[0] $point[1] $palette[0] }
} 'direct-geology-cairn-v3'

# Four silhouettes, four ecological reads: thorn, wet frond, low veil and warm
# bloom.  Every sprite is a native 16px cutout with a transparent border.
New-Texture 'rift_thorn' {
    param($bitmap, $graphics)
    $stem = Convert-Color '#4B4436'; $dark = Convert-Color '#2C302A'; $thorn = Convert-Color '#8B866A'; $tip = Convert-Color '#B7A676'
    foreach ($point in @(@(7,15),@(8,15),@(7,14),@(8,14),@(7,13),@(8,13),@(7,12),@(8,12),@(6,11),@(7,11),@(6,10),@(5,9),@(5,8),@(9,11),@(10,10),@(10,9),@(11,8),@(7,7),@(8,7),@(8,6),@(8,5),@(7,4),@(7,3))) {
        Set-SafePixel $bitmap $point[0] $point[1] $stem
    }
    foreach ($point in @(@(4,8),@(3,7),@(11,7),@(12,6),@(6,3),@(9,4),@(5,10),@(10,11))) { Set-SafePixel $bitmap $point[0] $point[1] $thorn }
    foreach ($point in @(@(3,6),@(13,5),@(5,2),@(10,3))) { Set-SafePixel $bitmap $point[0] $point[1] $tip }
    Set-SafePixel $bitmap 7 13 $dark; Set-SafePixel $bitmap 6 10 $dark
} 'direct-flora-rift-thorn-v3' $true

New-Texture 'mire_frond' {
    param($bitmap, $graphics)
    $stem = Convert-Color '#43543A'; $dark = Convert-Color '#2C4031'; $leaf = Convert-Color '#64805A'; $light = Convert-Color '#8FA574'
    for ($y = 3; $y -lt 16; $y++) { Set-SafePixel $bitmap 7 $y $stem; Set-SafePixel $bitmap 8 $y $stem }
    foreach ($frond in @(
        @(@(7,5),@(6,4),@(5,4),@(4,3)), @(@(8,6),@(9,5),@(10,5),@(11,4)),
        @(@(7,8),@(6,7),@(5,7),@(4,6),@(3,6)), @(@(8,9),@(9,8),@(10,8),@(11,7),@(12,7)),
        @(@(7,11),@(6,10),@(5,10),@(4,9)), @(@(8,12),@(9,11),@(10,11),@(11,10))
    )) {
        foreach ($point in $frond) { Set-SafePixel $bitmap $point[0] $point[1] $leaf }
    }
    foreach ($point in @(@(4,3),@(11,4),@(3,6),@(12,7),@(4,9),@(11,10),@(7,3))) { Set-SafePixel $bitmap $point[0] $point[1] $light }
    Set-SafePixel $bitmap 7 14 $dark
} 'direct-flora-mire-frond-v3' $true

New-Texture 'mossveil' {
    param($bitmap, $graphics)
    $dark = Convert-Color '#314631'; $base = Convert-Color '#4F6845'; $mid = Convert-Color '#6E8759'; $light = Convert-Color '#96A873'
    foreach ($box in @(@(3,5,10,4),@(1,8,14,3),@(4,11,3,4),@(9,10,3,5))) {
        for ($y = $box[1]; $y -lt ($box[1] + $box[3]); $y++) { for ($x = $box[0]; $x -lt ($box[0] + $box[2]); $x++) { Set-SafePixel $bitmap $x $y $base } }
    }
    foreach ($point in @(@(2,9),@(4,6),@(7,5),@(11,6),@(13,9),@(5,12),@(10,12))) { Set-SafePixel $bitmap $point[0] $point[1] $light }
    foreach ($point in @(@(1,10),@(3,8),@(8,8),@(12,10),@(4,14),@(9,14),@(11,13))) { Set-SafePixel $bitmap $point[0] $point[1] $dark }
    foreach ($point in @(@(5,7),@(9,6),@(6,10),@(11,9))) { Set-SafePixel $bitmap $point[0] $point[1] $mid }
} 'direct-flora-mossveil-v3' $true

New-Texture 'amber_bloom' {
    param($bitmap, $graphics)
    $stem = Convert-Color '#4D653C'; $leaf = Convert-Color '#70884B'; $petal = Convert-Color '#D99A3C'; $light = Convert-Color '#F3C65A'; $heart = Convert-Color '#7A4A2B'
    for ($y = 7; $y -lt 16; $y++) { Set-SafePixel $bitmap 7 $y $stem; Set-SafePixel $bitmap 8 $y $stem }
    foreach ($point in @(@(6,12),@(5,11),@(4,11),@(9,10),@(10,9),@(11,9))) { Set-SafePixel $bitmap $point[0] $point[1] $leaf }
    foreach ($point in @(@(7,3),@(8,3),@(6,4),@(7,4),@(8,4),@(9,4),@(5,5),@(6,5),@(7,5),@(8,5),@(9,5),@(10,5),@(6,6),@(7,6),@(8,6),@(9,6),@(7,7),@(8,7))) { Set-SafePixel $bitmap $point[0] $point[1] $petal }
    foreach ($point in @(@(7,3),@(5,5),@(10,5),@(8,7))) { Set-SafePixel $bitmap $point[0] $point[1] $light }
    Set-SafePixel $bitmap 7 5 $heart; Set-SafePixel $bitmap 8 5 $heart
} 'direct-flora-amber-bloom-v3' $true

function New-TurfFamily(
    [string]$Name,
    [string[]]$TopPalette,
    [string[]]$SoilPalette,
    [int]$Seed
) {
    New-NaturalMaterial "$Name`_top" $TopPalette $Seed 'organic'
    $topColors = @($TopPalette | ForEach-Object { Convert-Color $_ })
    $soilColors = @($SoilPalette | ForEach-Object { Convert-Color $_ })
    New-Texture "$Name`_side" {
        param($bitmap, $graphics)
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                $hash = Get-ArtHash $x $y ($Seed + 1)
                $bitmap.SetPixel($x, $y, $soilColors[1 + ($hash % 3)])
            }
        }
        $capDepths = @(4,3,4,5,4,3,3,4,5,4,3,4,4,5,3,4)
        for ($x = 0; $x -lt 16; $x++) {
            for ($y = 0; $y -lt $capDepths[$x]; $y++) {
                $bitmap.SetPixel($x, $y, $topColors[1 + ((Get-ArtHash $x $y ($Seed + 3)) % 3)])
            }
        }
        foreach ($root in @(@(1,4),@(1,5),@(5,3),@(5,4),@(9,4),@(9,5),@(13,3),@(13,4),@(13,5))) {
            Set-SafePixel $bitmap $root[0] $root[1] $topColors[0]
        }
        foreach ($fleck in @(@(3,9),@(7,13),@(11,8),@(14,12))) {
            Set-SafePixel $bitmap $fleck[0] $fleck[1] $soilColors[4]
        }
    } 'direct-turf-side-v3'
}

New-TurfFamily 'rootfelt' @('#3C5932','#517040','#688B50','#83A061','#A38D57') @('#513B28','#6B4E33','#866542','#A17F57','#BEA071') 211
New-TurfFamily 'dried_ichor' @('#4B512F','#667043','#808252','#9A925B','#BE945B') @('#57352B','#704638','#8B5A46','#A87358','#C08D69') 223
New-TurfFamily 'marrowstone' @('#58664B','#71805D','#8C9870','#A8B184','#D3C79D') @('#513B28','#6B4E33','#866542','#A17F57','#BEA071') 227
New-TurfFamily 'suture_silt' @('#36513A','#4E6C4B','#68875D','#86A170','#B09B62') @('#513B28','#6B4E33','#866542','#A17F57','#BEA071') 229

New-Texture 'ashen_sod_side' {
    param($bitmap, $graphics)
    $soil = @('#4C392A','#634A34','#7C6044','#977757') | ForEach-Object { Convert-Color $_ }
    $grass = @('#3D5B31','#54763E','#6F944E','#88AA61') | ForEach-Object { Convert-Color $_ }
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $hash = Get-ArtHash $x $y 181
            $bitmap.SetPixel($x, $y, $soil[1 + ($hash % 3)])
        }
    }
    $capDepths = @(4,4,3,4,5,4,3,3,4,5,4,4,3,5,4,3)
    for ($x = 0; $x -lt 16; $x++) {
        for ($y = 0; $y -lt $capDepths[$x]; $y++) {
            $bitmap.SetPixel($x, $y, $grass[1 + ((Get-ArtHash $x $y 183) % 3)])
        }
    }
    foreach ($root in @(@(2,4),@(2,5),@(6,3),@(6,4),@(10,4),@(10,5),@(10,6),@(14,3),@(14,4))) {
        Set-SafePixel $bitmap $root[0] $root[1] $grass[0]
    }
} 'direct-natural-reference-v3'

# Functional blocks: each face describes use at a glance at vanilla 16px scale.
foreach ($face in @('bottom','top','front','side')) {
    New-Texture "gravework_bench_$face" {
        param($bitmap, $graphics)
        Fill-Rect $graphics '#3A2B20' 0 0 16 16
        Fill-Rect $graphics '#6F5035' 1 1 14 14
        Fill-Rect $graphics '#8C6947' 2 2 12 2
        if ($face -eq 'top') {
            Fill-Rect $graphics '#9B7952' 1 1 14 14
            Fill-Rect $graphics '#5D432D' 7 1 2 14
            Fill-Rect $graphics '#5D432D' 1 7 14 2
            Fill-Rect $graphics '#C0A06C' 2 2 5 1
            Fill-Rect $graphics '#3C4542' 10 3 3 8
            Fill-Rect $graphics '#B8C0AD' 11 4 1 6
            foreach ($point in @(@(3,4),@(5,11),@(11,12),@(13,5))) {
                $bitmap.SetPixel($point[0], $point[1], (Convert-Color '#D1B77D'))
            }
        }
        elseif ($face -eq 'front') {
            Fill-Rect $graphics '#4B3426' 2 5 12 8
            Fill-Rect $graphics '#2E3431' 3 6 10 3
            Fill-Rect $graphics '#707A70' 4 6 8 1
            Fill-Rect $graphics '#6B4A31' 3 10 4 3
            Fill-Rect $graphics '#6B4A31' 9 10 4 3
            Fill-Rect $graphics '#D2B56F' 5 11 1 1
            Fill-Rect $graphics '#D2B56F' 10 11 1 1
        }
        elseif ($face -eq 'side') {
            Fill-Rect $graphics '#4B3426' 2 5 12 8
            Fill-Rect $graphics '#B4A06E' 3 6 2 6
            Fill-Rect $graphics '#B4A06E' 11 6 2 6
            Fill-Rect $graphics '#5A3028' 5 8 6 2
        }
        else {
            Fill-Rect $graphics '#4B3426' 2 3 12 10
            Fill-Rect $graphics '#2C322F' 4 5 8 6
        }
    } 'direct-station-gravework-v3'
}

foreach ($face in @('top','bottom','side','front','front_on')) {
    New-Texture "pitch_kiln_$face" {
        param($bitmap, $graphics)
        Fill-Rect $graphics '#3C403E' 0 0 16 16
        foreach ($patch in @(@(1,1,5,3),@(8,1,6,3),@(3,5,6,3),@(11,6,4,3),@(1,10,6,4),@(9,11,6,4))) {
            Fill-Rect $graphics '#565C57' $patch[0] $patch[1] $patch[2] $patch[3]
        }
        foreach ($point in @(@(2,2),@(9,3),@(5,6),@(13,8),@(4,12),@(11,13))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-Color '#7C8178'))
        }
        if ($face -eq 'front' -or $face -eq 'front_on') {
            Fill-Rect $graphics '#2A2C2A' 3 4 10 10
            Fill-Rect $graphics '#171A19' 4 6 8 7
            Fill-Rect $graphics '#6D5540' 4 4 8 2
            Fill-Rect $graphics '#8D653F' 3 5 1 8
            Fill-Rect $graphics '#8D653F' 12 5 1 8
            if ($face -eq 'front_on') {
                Fill-Rect $graphics '#B84D2C' 5 9 6 4
                Fill-Rect $graphics '#E78939' 6 8 4 4
                Fill-Rect $graphics '#FFD074' 7 9 2 2
            }
        }
        elseif ($face -eq 'top') {
            Fill-Rect $graphics '#262B29' 3 3 10 10
            Fill-Rect $graphics '#74776A' 4 4 8 1
            Fill-Rect $graphics '#151817' 6 6 4 4
        }
        elseif ($face -eq 'bottom') {
            Fill-Rect $graphics '#2A2E2C' 2 2 12 12
            Fill-Rect $graphics '#62675F' 4 4 8 8
        }
    } 'direct-station-kiln-v3'
}

New-Texture 'field_kitchen' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#34281F' 0 0 16 16
    Fill-Rect $graphics '#76563A' 1 1 14 14
    Fill-Rect $graphics '#A27E55' 1 1 14 3
    Fill-Rect $graphics '#4B5350' 2 5 12 8
    Fill-Rect $graphics '#7F8880' 3 5 10 2
    Fill-Rect $graphics '#252A28' 4 8 8 4
    Fill-Rect $graphics '#B8522F' 6 9 4 3
    Fill-Rect $graphics '#F0A34D' 7 9 2 2
    Fill-Rect $graphics '#2C261F' 3 14 3 2
    Fill-Rect $graphics '#2C261F' 10 14 3 2
} 'direct-station-kitchen-front-v3'

New-Texture 'field_kitchen_side' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#34281F' 0 0 16 16
    Fill-Rect $graphics '#76563A' 1 1 14 14
    Fill-Rect $graphics '#A27E55' 1 1 14 3
    Fill-Rect $graphics '#4B3828' 3 5 10 8
    Fill-Rect $graphics '#B79A69' 4 6 2 6
    Fill-Rect $graphics '#B79A69' 10 6 2 6
    Fill-Rect $graphics '#5D4631' 5 8 6 2
} 'direct-station-kitchen-side-v3'

New-Texture 'field_kitchen_top' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#A27E55' 0 0 16 16
    Fill-Rect $graphics '#C09B6B' 1 1 14 2
    Fill-Rect $graphics '#5E6661' 3 3 10 10
    Fill-Rect $graphics '#252A28' 5 5 6 6
    Fill-Rect $graphics '#8D9890' 5 5 6 1
    Fill-Rect $graphics '#9D5437' 6 7 4 3
    Fill-Rect $graphics '#E0A15A' 7 7 2 2
} 'direct-station-kitchen-top-v3'

New-Texture 'reliquary_crate' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#3A281B' 0 0 16 16
    Fill-Rect $graphics '#815C38' 1 1 14 14
    Fill-Rect $graphics '#A27A4B' 2 2 12 3
    Fill-Rect $graphics '#5C4028' 2 7 12 2
    Fill-Rect $graphics '#5C4028' 2 12 12 2
    Fill-Rect $graphics '#C19B64' 3 3 1 10
    Fill-Rect $graphics '#68452C' 11 2 2 13
} 'direct-container-reliquary-side-v3'

New-Texture 'reliquary_crate_front' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#3A281B' 0 0 16 16
    Fill-Rect $graphics '#815C38' 1 1 14 14
    Fill-Rect $graphics '#A27A4B' 2 2 12 3
    Fill-Rect $graphics '#5C4028' 1 7 14 2
    Fill-Rect $graphics '#5C4028' 1 12 14 2
    Fill-Rect $graphics '#C19B64' 3 3 1 10
    Fill-Rect $graphics '#C19B64' 12 3 1 10
    Fill-Rect $graphics '#3D4541' 6 6 4 5
    Fill-Rect $graphics '#B6B39B' 7 7 2 1
    Fill-Rect $graphics '#191C1B' 7 9 2 2
} 'direct-container-reliquary-front-v3'

New-Texture 'reliquary_crate_top' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#3A281B' 0 0 16 16
    Fill-Rect $graphics '#815C38' 1 1 14 14
    Fill-Rect $graphics '#A27A4B' 2 2 12 3
    Fill-Rect $graphics '#5C4028' 1 7 14 2
    Fill-Rect $graphics '#C19B64' 3 3 1 10
    Fill-Rect $graphics '#C19B64' 12 3 1 10
} 'direct-container-reliquary-top-v3'

New-Texture 'reliquary_crate_metal' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#303734' 0 0 16 16
    Fill-Rect $graphics '#69716A' 1 1 14 14
    Fill-Rect $graphics '#A5AA99' 2 2 12 2
    Fill-Rect $graphics '#3E4742' 3 6 10 7
} 'direct-container-reliquary-metal-v3'

New-Texture 'tallow_lantern' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#2D312E' 0 0 16 16
    Fill-Rect $graphics '#596159' 1 1 14 14
    Fill-Rect $graphics '#E7B95E' 4 3 8 10
    Fill-Rect $graphics '#FFE6A0' 6 4 4 8
    Fill-Rect $graphics '#383D39' 0 0 16 3
    Fill-Rect $graphics '#383D39' 0 13 16 3
} 'direct-light-lantern-icon-v3'

New-Texture 'tallow_lantern_frame' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#333835' 0 0 16 16
    Fill-Rect $graphics '#626A62' 2 2 12 12
    Fill-Rect $graphics '#9A9D8B' 3 3 10 2
    Fill-Rect $graphics '#414844' 4 5 8 8
} 'direct-light-lantern-frame-v3'

New-Texture 'tallow_lantern_glow' {
    param($bitmap, $graphics)
    Fill-Rect $graphics '#D8943D' 0 0 16 16
    Fill-Rect $graphics '#F0BB59' 2 1 12 14
    Fill-Rect $graphics '#FFE8A0' 5 2 6 12
    Fill-Rect $graphics '#FFF4C2' 7 3 2 10
} 'direct-light-lantern-glow-v3'

function New-CutPlanksIfPresent([string]$Name, [string[]]$Palette, [int]$Seed) {
    $path = Join-Path $script:ProjectRoot "src\main\resources\assets\gravesown\textures\block\$Name.png"
    if (-not (Test-Path -LiteralPath $path)) { return }
    New-Texture $Name {
        param($bitmap, $graphics)
        Fill-Rect $graphics $Palette[1] 0 0 16 16
        foreach ($y in @(0,5,10,15)) { Fill-Rect $graphics $Palette[0] 0 $y 16 1 }
        foreach ($segment in @(@(1,2,5),@(8,3,6),@(2,7,7),@(11,8,4),@(1,12,4),@(7,13,7))) {
            Fill-Rect $graphics $Palette[2] $segment[0] $segment[1] $segment[2] 1
        }
        foreach ($point in @(@(3,3),@(12,4),@(5,8),@(13,12),@(8,14))) {
            $bitmap.SetPixel($point[0], $point[1], (Convert-Color $Palette[3]))
        }
    } 'direct-sawmill-cut-planks-v3'
}

New-CutPlanksIfPresent 'ribroot_cut_planks'   @('#38281F','#6C4D35','#916D49','#B79262') 211
New-CutPlanksIfPresent 'emberbark_cut_planks' @('#4B2D20','#94502E','#BE7141','#DEA064') 213
New-CutPlanksIfPresent 'palevine_cut_planks'  @('#5F5848','#A89C78','#C9BC91','#E2D5AC') 217
New-CutPlanksIfPresent 'cairnwood_cut_planks' @('#494034','#766752','#99876A','#BDAA86') 219
New-CutPlanksIfPresent 'suturewood_cut_planks' @('#302A20','#58482F','#75613F','#998158') 223
New-CutPlanksIfPresent 'mosswake_cut_planks'   @('#403925','#706139','#8D7E4B','#B1A067') 227
New-CutPlanksIfPresent 'sunveil_cut_planks'    @('#5B472C','#9B7945','#BD985A','#D9BC7B') 229

function New-GlassIfPresent([string]$Name, [string]$Edge, [string]$Glint, [int]$Alpha) {
    New-Texture $Name {
        param($bitmap, $graphics)
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edgeColor = Convert-Color $Edge
        $glintColor = Convert-Color $Glint
        $edgeAlpha = [System.Drawing.Color]::FromArgb($Alpha, $edgeColor.R, $edgeColor.G, $edgeColor.B)
        $glintAlpha = [System.Drawing.Color]::FromArgb([Math]::Min(255, $Alpha + 55), $glintColor.R, $glintColor.G, $glintColor.B)
        for ($i = 0; $i -lt 16; $i++) {
            $bitmap.SetPixel($i, 0, $edgeAlpha); $bitmap.SetPixel($i, 15, $edgeAlpha)
            $bitmap.SetPixel(0, $i, $edgeAlpha); $bitmap.SetPixel(15, $i, $edgeAlpha)
        }
        foreach ($point in @(@(3,3),@(4,3),@(3,4),@(10,8),@(11,8),@(11,9))) {
            $bitmap.SetPixel($point[0], $point[1], $glintAlpha)
        }
    } 'direct-glass-v3' $true
}

New-GlassIfPresent 'gravesown_glass' '#789D8A' '#D7E6C3' 105
New-GlassIfPresent 'tempered_glass' '#536C63' '#D9DCCB' 155

function Paint-SawmillFace([string]$name) {
    New-Texture $name {
        param($bitmap, $graphics)
        Fill-Rect $graphics '#493629' 0 0 16 16
        Fill-Rect $graphics '#8A6543' 1 1 14 14
        Fill-Rect $graphics '#B38B5C' 2 2 12 2
        if ($name -match 'front') {
            Fill-Rect $graphics '#39413F' 2 5 12 8
            Fill-Rect $graphics '#AEB4A7' 3 6 10 2
            Fill-Rect $graphics '#222725' 5 9 6 3
            Fill-Rect $graphics '#B85A32' 7 10 2 2
        }
        elseif ($name -match 'top') {
            Fill-Rect $graphics '#B18C60' 1 1 14 14
            Fill-Rect $graphics '#353C3A' 7 1 2 14
            foreach ($y in 3..12) {
                $x = if (($y % 2) -eq 0) { 6 } else { 9 }
                $bitmap.SetPixel($x, $y, (Convert-Color '#D7D8C9'))
            }
        }
        elseif ($name -match 'side') {
            Fill-Rect $graphics '#5D442F' 2 5 12 8
            Fill-Rect $graphics '#C0A16C' 3 6 2 6
            Fill-Rect $graphics '#C0A16C' 11 6 2 6
        }
    } 'direct-station-sawmill-v3'
}

foreach ($sawmillFace in @('sawmill_bottom','sawmill_side','sawmill_top','sawmill_front')) {
    Paint-SawmillFace $sawmillFace
}
New-Texture 'sawmill_blade' {
    param($bitmap, $graphics)
    $graphics.Clear([System.Drawing.Color]::Transparent)
    $blade = Convert-Color '#BFC4B8'
    $shadow = Convert-Color '#69716D'
    $hub = Convert-Color '#3C4542'
    foreach ($span in @(
        @(6,1,4),@(4,2,8),@(3,3,10),@(2,4,12),@(1,6,14),
        @(1,7,14),@(1,8,14),@(1,9,14),@(2,11,12),@(3,12,10),@(4,13,8),@(6,14,4)
    )) {
        for ($x = $span[0]; $x -lt ($span[0] + $span[2]); $x++) {
            $bitmap.SetPixel($x, $span[1], $blade)
        }
    }
    foreach ($point in @(@(6,1),@(9,1),@(3,3),@(12,3),@(1,6),@(14,9),@(3,12),@(11,13))) {
        $bitmap.SetPixel($point[0], $point[1], $shadow)
    }
    Fill-Rect $graphics '#3C4542' 6 6 4 4
    Fill-Rect $graphics '#D4A653' 7 7 2 2
} 'direct-station-sawmill-blade-v3' $true

function Write-GeneratedText([string]$RelativePath, [string]$Text) {
    $path = Join-Path $script:ProjectRoot $RelativePath
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
    [System.IO.File]::WriteAllText($path, $Text.Trim() + [Environment]::NewLine, [System.Text.UTF8Encoding]::new($false))
}

# Same registry/model ids, but silhouettes now communicate function in-world and in
# the inventory.  The reliquary is deliberately inset with lid, bands and lock.
Write-GeneratedText 'src\main\resources\assets\gravesown\models\block\field_kitchen.json' @'
{
  "textures": {
    "front": "gravesown:block/field_kitchen",
    "side": "gravesown:block/field_kitchen_side",
    "top": "gravesown:block/field_kitchen_top",
    "particle": "gravesown:block/field_kitchen_side"
  },
  "elements": [
    { "from": [2, 1, 2], "to": [14, 10, 14], "faces": {
      "down": { "texture": "#side" }, "up": { "texture": "#top" },
      "north": { "texture": "#front" }, "south": { "texture": "#front" },
      "west": { "texture": "#side" }, "east": { "texture": "#side" }
    }},
    { "from": [1, 10, 1], "to": [15, 12, 15], "faces": {
      "down": { "texture": "#side" }, "up": { "texture": "#top" },
      "north": { "texture": "#side" }, "south": { "texture": "#side" },
      "west": { "texture": "#side" }, "east": { "texture": "#side" }
    }},
    { "from": [4, 12, 4], "to": [12, 15, 12], "faces": {
      "down": { "texture": "#side" }, "up": { "texture": "#top" },
      "north": { "texture": "#front" }, "south": { "texture": "#front" },
      "west": { "texture": "#side" }, "east": { "texture": "#side" }
    }}
  ]
}
'@

Write-GeneratedText 'src\main\resources\assets\gravesown\models\block\reliquary_crate.json' @'
{
  "textures": {
    "side": "gravesown:block/reliquary_crate",
    "front": "gravesown:block/reliquary_crate_front",
    "top": "gravesown:block/reliquary_crate_top",
    "metal": "gravesown:block/reliquary_crate_metal",
    "particle": "gravesown:block/reliquary_crate"
  },
  "elements": [
    { "from": [2, 0, 2], "to": [14, 14, 14], "faces": {
      "down": { "texture": "#side" }, "up": { "texture": "#top" },
      "north": { "texture": "#front" }, "south": { "texture": "#side" },
      "west": { "texture": "#side" }, "east": { "texture": "#side" }
    }},
    { "from": [1, 13, 1], "to": [15, 16, 15], "faces": {
      "down": { "texture": "#side" }, "up": { "texture": "#top" },
      "north": { "texture": "#front" }, "south": { "texture": "#side" },
      "west": { "texture": "#side" }, "east": { "texture": "#side" }
    }},
    { "from": [1.5, 4, 1.5], "to": [14.5, 5, 14.5], "faces": {
      "down": { "texture": "#metal" }, "up": { "texture": "#metal" },
      "north": { "texture": "#metal" }, "south": { "texture": "#metal" },
      "west": { "texture": "#metal" }, "east": { "texture": "#metal" }
    }},
    { "from": [1.5, 10, 1.5], "to": [14.5, 11, 14.5], "faces": {
      "down": { "texture": "#metal" }, "up": { "texture": "#metal" },
      "north": { "texture": "#metal" }, "south": { "texture": "#metal" },
      "west": { "texture": "#metal" }, "east": { "texture": "#metal" }
    }},
    { "from": [6, 6, 1], "to": [10, 11, 2], "faces": {
      "down": { "texture": "#metal" }, "up": { "texture": "#metal" },
      "north": { "texture": "#metal" }, "south": { "texture": "#metal" },
      "west": { "texture": "#metal" }, "east": { "texture": "#metal" }
    }}
  ]
}
'@

Write-GeneratedText 'src\main\resources\assets\gravesown\models\block\tallow_lantern.json' @'
{
  "ambientocclusion": false,
  "textures": {
    "frame": "gravesown:block/tallow_lantern_frame",
    "glow": "gravesown:block/tallow_lantern_glow",
    "particle": "gravesown:block/tallow_lantern_frame"
  },
  "elements": [
    { "from": [5, 3, 5], "to": [11, 13, 11], "faces": {
      "down": { "texture": "#glow" }, "up": { "texture": "#glow" },
      "north": { "texture": "#glow" }, "south": { "texture": "#glow" },
      "west": { "texture": "#glow" }, "east": { "texture": "#glow" }
    }},
    { "from": [3, 2, 3], "to": [13, 4, 13], "faces": {
      "down": { "texture": "#frame" }, "up": { "texture": "#frame" },
      "north": { "texture": "#frame" }, "south": { "texture": "#frame" },
      "west": { "texture": "#frame" }, "east": { "texture": "#frame" }
    }},
    { "from": [3, 12, 3], "to": [13, 14, 13], "faces": {
      "down": { "texture": "#frame" }, "up": { "texture": "#frame" },
      "north": { "texture": "#frame" }, "south": { "texture": "#frame" },
      "west": { "texture": "#frame" }, "east": { "texture": "#frame" }
    }},
    { "from": [3, 4, 3], "to": [5, 12, 5], "faces": {
      "north": { "texture": "#frame" }, "south": { "texture": "#frame" },
      "west": { "texture": "#frame" }, "east": { "texture": "#frame" }
    }},
    { "from": [11, 4, 11], "to": [13, 12, 13], "faces": {
      "north": { "texture": "#frame" }, "south": { "texture": "#frame" },
      "west": { "texture": "#frame" }, "east": { "texture": "#frame" }
    }}
  ]
}
'@

Write-GeneratedText 'src\main\resources\assets\gravesown\models\block\sawmill.json' @'
{
  "render_type": "minecraft:cutout",
  "textures": {
    "bottom": "gravesown:block/sawmill_bottom",
    "side": "gravesown:block/sawmill_side",
    "front": "gravesown:block/sawmill_front",
    "top": "gravesown:block/sawmill_top",
    "blade": "gravesown:block/sawmill_blade",
    "particle": "gravesown:block/sawmill_side"
  },
  "elements": [
    { "from": [1, 0, 1], "to": [15, 8, 15], "faces": {
      "down": { "texture": "#bottom" }, "up": { "texture": "#top" },
      "north": { "texture": "#front" }, "south": { "texture": "#side" },
      "west": { "texture": "#side" }, "east": { "texture": "#side" }
    }},
    { "from": [0, 8, 0], "to": [16, 10, 16], "faces": {
      "down": { "texture": "#side" }, "up": { "texture": "#top" },
      "north": { "texture": "#side" }, "south": { "texture": "#side" },
      "west": { "texture": "#side" }, "east": { "texture": "#side" }
    }},
    { "from": [7.5, 9, 3], "to": [8.5, 16, 13], "faces": {
      "north": { "texture": "#blade" }, "south": { "texture": "#blade" },
      "west": { "texture": "#blade" }, "east": { "texture": "#blade" }
    }}
  ]
}
'@

Write-GeneratedText 'src\main\resources\assets\gravesown\models\block\gravesown_glass.json' @'
{
  "parent": "minecraft:block/cube_all",
  "render_type": "minecraft:translucent",
  "textures": { "all": "gravesown:block/gravesown_glass" }
}
'@

Write-GeneratedText 'src\main\resources\assets\gravesown\models\block\tempered_glass.json' @'
{
  "parent": "minecraft:block/cube_all",
  "render_type": "minecraft:translucent",
  "textures": { "all": "gravesown:block/tempered_glass" }
}
'@

foreach ($turf in @(
    @('rootfelt','fibrous_loam'),
    @('dried_ichor','scar_shale'),
    @('marrowstone','fibrous_loam'),
    @('suture_silt','fibrous_loam')
)) {
    $id = $turf[0]
    $bottom = $turf[1]
    Write-GeneratedText "src\main\resources\assets\gravesown\models\block\$id.json" @"
{
  "parent": "minecraft:block/cube_bottom_top",
  "textures": {
    "bottom": "gravesown:block/$bottom",
    "side": "gravesown:block/${id}_side",
    "top": "gravesown:block/${id}_top"
  }
}
"@
}

foreach ($id in @('veined_shale','splintered_marrowstone','cairnstone')) {
    Write-GeneratedText "src\main\resources\assets\gravesown\blockstates\$id.json" @"
{
  "variants": { "": { "model": "gravesown:block/$id" } }
}
"@
    Write-GeneratedText "src\main\resources\assets\gravesown\models\block\$id.json" @"
{
  "parent": "minecraft:block/cube_all",
  "textures": { "all": "gravesown:block/$id" }
}
"@
    Write-GeneratedText "src\main\resources\assets\gravesown\models\item\$id.json" @"
{ "parent": "gravesown:block/$id" }
"@
    Write-GeneratedText "src\main\resources\data\gravesown\loot_table\blocks\$id.json" @"
{
  "type": "minecraft:block",
  "pools": [{
    "bonus_rolls": 0,
    "conditions": [{ "condition": "minecraft:survives_explosion" }],
    "entries": [{ "type": "minecraft:item", "name": "gravesown:$id" }],
    "rolls": 1
  }]
}
"@
}

foreach ($id in @('rift_thorn','mire_frond','mossveil','amber_bloom')) {
    Write-GeneratedText "src\main\resources\assets\gravesown\blockstates\$id.json" @"
{
  "variants": { "": { "model": "gravesown:block/$id" } }
}
"@
    Write-GeneratedText "src\main\resources\assets\gravesown\models\block\$id.json" @"
{
  "parent": "minecraft:block/cross",
  "render_type": "minecraft:cutout",
  "textures": { "cross": "gravesown:block/$id" }
}
"@
    Write-GeneratedText "src\main\resources\assets\gravesown\models\item\$id.json" @"
{
  "parent": "minecraft:item/generated",
  "textures": { "layer0": "gravesown:block/$id" }
}
"@
    Write-GeneratedText "src\main\resources\data\gravesown\loot_table\blocks\$id.json" @"
{
  "type": "minecraft:block",
  "pools": [{
    "bonus_rolls": 0,
    "conditions": [{ "condition": "minecraft:survives_explosion" }],
    "entries": [{ "type": "minecraft:item", "name": "gravesown:$id" }],
    "rolls": 1
  }]
}
"@
}

function Apply-ColorGrade(
    [string]$Path,
    [float]$RedScale,
    [float]$GreenScale,
    [float]$BlueScale,
    [float]$RedLift,
    [float]$GreenLift,
    [float]$BlueLift
) {
    $source = [System.Drawing.Bitmap]::FromFile($Path)
    $output = New-ArgbBitmap $source.Width $source.Height
    $graphics = [System.Drawing.Graphics]::FromImage($output)
    $attributes = [System.Drawing.Imaging.ImageAttributes]::new()
    try {
        $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
        $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighSpeed
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None

        $matrix = [System.Drawing.Imaging.ColorMatrix]::new()
        $matrix.Matrix00 = $RedScale
        $matrix.Matrix11 = $GreenScale
        $matrix.Matrix22 = $BlueScale
        $matrix.Matrix33 = 1.0
        $matrix.Matrix44 = 1.0
        $matrix.Matrix40 = $RedLift
        $matrix.Matrix41 = $GreenLift
        $matrix.Matrix42 = $BlueLift
        $attributes.SetColorMatrix($matrix)
        $graphics.DrawImage(
            $source,
            [System.Drawing.Rectangle]::new(0, 0, $source.Width, $source.Height),
            0, 0, $source.Width, $source.Height,
            [System.Drawing.GraphicsUnit]::Pixel,
            $attributes
        )
    }
    finally {
        $attributes.Dispose()
        $graphics.Dispose()
        $source.Dispose()
    }
    try { $output.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png) }
    finally { $output.Dispose() }
}

# Gameplay textures deliberately receive no global colour transform here. The cold
# navy palette belongs only to GUI and launcher presentation assets.

function Get-RelativeArtPath([string]$Path) {
    $root = $script:ProjectRoot.TrimEnd('\') + '\'
    ([System.IO.Path]::GetFullPath($Path)).Substring($root.Length).Replace('\','/')
}

function Get-ShippedPngs {
    $roots = @(
        'src\main\resources\assets\gravesown',
        'src\main\resources\assets\minecraft'
    ) | ForEach-Object { Join-Path $script:ProjectRoot $_ }
    $files = foreach ($root in $roots) {
        if (Test-Path -LiteralPath $root) {
            Get-ChildItem -LiteralPath $root -Recurse -File -Filter '*.png'
        }
    }
    $icon = Join-Path $script:ProjectRoot 'src\main\resources\gravesown.png'
    if (Test-Path -LiteralPath $icon) { $files += Get-Item -LiteralPath $icon }
    @($files | Sort-Object FullName -Unique)
}

$coverage = [System.Collections.Generic.List[object]]::new()
foreach ($file in Get-ShippedPngs) {
    $full = [System.IO.Path]::GetFullPath($file.FullName)
    $relative = Get-RelativeArtPath $full
    if ($script:DirectPaths.Contains($full)) {
        $bitmap = [System.Drawing.Bitmap]::FromFile($full)
        try {
            $coverage.Add([pscustomobject]@{
                Path = $relative
                Treatment = $script:DirectTreatments[$full]
                Size = "$($bitmap.Width)x$($bitmap.Height)"
            })
        }
        finally { $bitmap.Dispose() }
        continue
    }

    if ($relative -match '/textures/(block|item|entity|models/armor)/') {
        # Gameplay art keeps the source-authored biome and material palette.
        $treatment = 'source-native-world-art-v3'
    }
    elseif ($relative -match '/textures/gui/' -or $relative -match '/assets/minecraft/textures/gui/') {
        # The UI generator owns exact navy/cyan state colours.  A second matrix
        # here would shift those reviewed hex values and makes overlapping full
        # generations non-deterministic, so V3 records coverage without touching
        # the already final pixels.
        $treatment = 'exact-cold-ui-noop-v3'
    }
    elseif ($relative -match '/assets/minecraft/textures/environment/') {
        # Sun and moon are also emitted in their final palette upstream.
        $treatment = 'exact-celestial-noop-v3'
    }
    else {
        Apply-ColorGrade $full 1.08 1.08 1.04 0.024 0.024 0.014
        $treatment = 'general-shipped-grade-v3'
    }

    $bitmap = [System.Drawing.Bitmap]::FromFile($full)
    try {
        $coverage.Add([pscustomobject]@{
            Path = $relative
            Treatment = $treatment
            Size = "$($bitmap.Width)x$($bitmap.Height)"
        })
    }
    finally { $bitmap.Dispose() }
}

$shipped = @(Get-ShippedPngs)
if ($coverage.Count -ne $shipped.Count) {
    throw "Art Language V3 coverage mismatch: covered $($coverage.Count), shipped $($shipped.Count)."
}

$reportDirectory = Join-Path $script:ProjectRoot 'build\reports\gravesown\art'
New-Item -ItemType Directory -Force -Path $reportDirectory | Out-Null
$reportPath = Join-Path $reportDirectory 'art-language-v3-coverage.tsv'
$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("path`ttreatment`tsize")
foreach ($row in $coverage) { $lines.Add("$($row.Path)`t$($row.Treatment)`t$($row.Size)") }
[System.IO.File]::WriteAllLines($reportPath, $lines, [System.Text.UTF8Encoding]::new($false))

$directCount = @($coverage | Where-Object { $_.Treatment -like 'direct-*' }).Count
$gradedCount = $coverage.Count - $directCount
Write-Host "PASS Art Language V3: $($coverage.Count) shipped PNGs covered ($directCount direct, $gradedCount UV-safe graded)." -ForegroundColor Green
Write-Host "Coverage report: $reportPath"
