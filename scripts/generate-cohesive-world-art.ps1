Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

# Texture Language V2 terrain generator.
#
# Terrain deliberately stays 16x16: matching vanilla Minecraft's visual pixel scale
# is more important than adding source pixels.  The old prototype painted repeated
# horizontal rectangles into every material, which made whole hills read as brick
# walls.  This pass instead uses a small, deterministic set of irregular clusters,
# sparse mineral accents and material-specific cracks/roots.  Each tile has its own
# local palette; there is no global gray/purple colour grade.

function Convert-Color([string]$Hex) {
    [System.Drawing.ColorTranslator]::FromHtml($Hex)
}

function Get-Hash([int]$X, [int]$Y, [int]$Seed) {
    [int64]$value = ([int64]$X * 73856093) -bxor ([int64]$Y * 19349663) -bxor ([int64]$Seed * 83492791)
    $value = ($value -bxor ($value -shr 13)) % 2147483647
    $value = (($value * 48271) + 12345) % 2147483647
    if ($value -lt 0) { $value = -$value }
    return [int]$value
}

function Set-SafePixel($Bitmap, [int]$X, [int]$Y, [System.Drawing.Color]$Color) {
    if ($X -ge 0 -and $X -lt 16 -and $Y -ge 0 -and $Y -lt 16) {
        $Bitmap.SetPixel($X, $Y, $Color)
    }
}

function Paint-Cluster(
    $Bitmap,
    [int]$X,
    [int]$Y,
    [System.Drawing.Color]$Color,
    [int]$Variant
) {
    # Five compact Minecraft-like cluster silhouettes.  None is a repeating rail.
    $shapes = @(
        @(@(0,0), @(1,0), @(0,1)),
        @(@(0,0), @(1,0), @(1,1), @(2,1)),
        @(@(0,0), @(0,1), @(1,1), @(1,2)),
        @(@(0,0), @(1,0), @(2,0), @(1,1)),
        @(@(0,0), @(1,0), @(0,1), @(1,1), @(2,1))
    )
    foreach ($point in $shapes[$Variant % $shapes.Count]) {
        Set-SafePixel $Bitmap ($X + $point[0]) ($Y + $point[1]) $Color
    }
}

function New-MaterialTile(
    [string]$Name,
    [string[]]$Palette,
    [int]$Seed,
    [ValidateSet('soil','stone','sand','organic','wood')][string]$Grammar = 'soil'
) {
    if ($Palette.Count -lt 5) {
        throw "Material '$Name' needs shadow, low, mid, light and accent colours."
    }

    $colors = @($Palette | ForEach-Object { Convert-Color $_ })
    $bitmap = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                $bitmap.SetPixel($x, $y, $colors[2])
            }
        }

        # Twelve broad, irregular value clusters.  Their positions are deterministic
        # but not aligned to a shared grid across materials.
        for ($index = 0; $index -lt 12; $index++) {
            $hash = Get-Hash $index ($Seed + $index * 3) $Seed
            $x = 1 + ($hash % 13)
            $y = 1 + (($hash -shr 5) % 13)
            $tone = if (($index % 5) -eq 0) { 0 }
                elseif (($index % 3) -eq 0) { 3 }
                else { 1 }
            Paint-Cluster $bitmap $x $y $colors[$tone] (($hash -shr 9) % 5)
        }

        # Sparse highlights and inclusions keep the surface readable from normal
        # distance without turning into one-pixel confetti.
        for ($index = 0; $index -lt 7; $index++) {
            $hash = Get-Hash ($Seed + 31) $index ($Seed + 7)
            $x = $hash % 16
            $y = ($hash -shr 7) % 16
            $accentIndex = if (($index % 3) -eq 0) { 4 } else { 3 }
            $bitmap.SetPixel($x, $y, $colors[$accentIndex])
        }

        switch ($Grammar) {
            'stone' {
                # Two short, branching cracks rather than horizontal masonry seams.
                foreach ($path in @(
                    @(@(2,2),@(3,3),@(4,4),@(4,5),@(5,5)),
                    @(@(12,9),@(11,10),@(10,10),@(9,11),@(9,12))
                )) {
                    foreach ($point in $path) {
                        Set-SafePixel $bitmap $point[0] $point[1] $colors[0]
                    }
                }
                Set-SafePixel $bitmap 5 4 $colors[3]
                Set-SafePixel $bitmap 10 9 $colors[3]
            }
            'soil' {
                foreach ($point in @(@(2,11),@(3,12),@(9,4),@(10,5),@(13,13))) {
                    Set-SafePixel $bitmap $point[0] $point[1] $colors[4]
                }
            }
            'sand' {
                foreach ($point in @(@(1,5),@(5,2),@(8,12),@(12,6),@(14,14),@(4,9))) {
                    Set-SafePixel $bitmap $point[0] $point[1] $colors[4]
                }
            }
            'organic' {
                foreach ($path in @(
                    @(@(3,1),@(3,2),@(4,3),@(4,4)),
                    @(@(12,11),@(11,12),@(11,13),@(10,14))
                )) {
                    foreach ($point in $path) {
                        Set-SafePixel $bitmap $point[0] $point[1] $colors[4]
                    }
                }
            }
            'wood' {
                foreach ($path in @(
                    @(@(2,1),@(2,2),@(3,3),@(3,4)),
                    @(@(12,8),@(11,9),@(11,10),@(10,11))
                )) {
                    foreach ($point in $path) {
                        Set-SafePixel $bitmap $point[0] $point[1] $colors[0]
                    }
                }
            }
        }

        $path = Join-Path $script:ProjectRoot "src\main\resources\assets\gravesown\textures\block\$Name.png"
        $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Host "Created $Name.png"
    }
    finally {
        $bitmap.Dispose()
    }
}

# Familiar natural materials first; Gravesown identity comes from local accents and
# neighboring ecology rather than a blanket purple/gray filter.
New-MaterialTile 'grave_loam'       @('#3B2D24','#5A4331','#73583F','#927255','#B19168')  11 'soil'
New-MaterialTile 'ashen_sod_top'    @('#34492C','#4E673B','#66824B','#82A45E','#A5BD72')  17 'organic'
New-MaterialTile 'hushstone'        @('#3D4145','#555B5F','#6D7376','#898F91','#A59E87')  23 'stone'
New-MaterialTile 'deep_hushstone'   @('#23292D','#353D42','#465158','#5D6970','#6F6257')  29 'stone'
New-MaterialTile 'gravebed'         @('#161B1E','#252D31','#343E43','#4A555A','#66564C')  31 'stone'
New-MaterialTile 'rootfelt'         @('#263A26','#3A5333','#4F6B43','#68895A','#8A7650')  37 'organic'
New-MaterialTile 'fibrous_loam'     @('#4A3725','#674B31','#806241','#9C7B56','#B79A70')  41 'soil'
New-MaterialTile 'scar_shale'       @('#4B2F28','#694035','#845244','#A56C58','#C28A67')  43 'stone'
New-MaterialTile 'marrowstone'      @('#756A50','#918469','#AA9B7C','#C6B994','#DDD1AC')  47 'stone'
New-MaterialTile 'suture_silt'      @('#29372C','#3D4B37','#526045','#6B7959','#89744C')  53 'soil'
New-MaterialTile 'dried_ichor'      @('#552D23','#733B2A','#925038','#B46945','#D18A58')  59 'organic'
New-MaterialTile 'abyssal_silt'     @('#182C29','#28433B','#385B4F','#507768','#79694A')  61 'soil'
New-MaterialTile 'brinebone'        @('#796F58','#95896D','#B0A27F','#CCBE99','#E1D2AA')  67 'stone'
New-MaterialTile 'gloam_muck'       @('#1D3028','#2E4738','#415E48','#5B775C','#7F704B')  71 'soil'
New-MaterialTile 'gloam_sand'       @('#756847','#94815A','#B09A6D','#CCB887','#E0CD9B')  73 'sand'
New-MaterialTile 'reliquary_crate'  @('#2C211B','#4A3325','#65482F','#8B6744','#B99661')  79 'wood'

# The sod side uses the exact same green and soil families as its top/bottom and a
# ragged root edge, so it reads like vanilla grass rather than a striped wall.
$side = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
try {
    $soil = @('#3B2D24','#5A4331','#73583F','#927255') | ForEach-Object { Convert-Color $_ }
    $grass = @('#34492C','#4E673B','#66824B','#82A45E') | ForEach-Object { Convert-Color $_ }
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $hash = Get-Hash $x $y 83
            $side.SetPixel($x, $y, $soil[1 + ($hash % 3)])
        }
    }
    for ($x = 0; $x -lt 16; $x++) {
        $depth = 3 + ((Get-Hash $x 2 89) % 3)
        for ($y = 0; $y -lt $depth; $y++) {
            $side.SetPixel($x, $y, $grass[1 + ((Get-Hash $x $y 97) % 3)])
        }
    }
    foreach ($root in @(@(2,4),@(2,5),@(6,3),@(6,4),@(6,5),@(10,4),@(10,5),@(10,6),@(14,3),@(14,4))) {
        Set-SafePixel $side $root[0] $root[1] $grass[0]
    }
    $path = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\textures\block\ashen_sod_side.png'
    $side.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
}
finally {
    $side.Dispose()
}

Write-Host 'PASS generated Texture Language V2 terrain: natural local palettes and irregular vanilla-scale clusters.' -ForegroundColor Green
