Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

# Texture Language V2 creature pass.  Geometry owns the silhouette; these atlases
# describe believable hide, bone and healed tissue at a Minecraft-readable scale.
# Large 3-7 pixel clusters replace the flat colour panels and one-pixel noise of the
# prototype.  Every atlas remains opaque so an unused UV can never disappear.

function C([string]$Hex) { [System.Drawing.ColorTranslator]::FromHtml($Hex) }

function Get-ArtHash([int]$X, [int]$Y, [int]$Seed) {
    [int64]$value = ([int64]$X * 73856093) -bxor ([int64]$Y * 19349663) -bxor ([int64]$Seed * 83492791)
    $value = ($value -bxor ($value -shr 11)) % 2147483647
    $value = (($value * 48271) + 12345) % 2147483647
    if ($value -lt 0) { $value = -$value }
    [int]$value
}

function Set-Pixel($Bitmap, [int]$X, [int]$Y, [System.Drawing.Color]$Color) {
    if ($X -ge 0 -and $Y -ge 0 -and $X -lt $Bitmap.Width -and $Y -lt $Bitmap.Height) {
        $Bitmap.SetPixel($X, $Y, $Color)
    }
}

function Fill-Region(
    $Bitmap,
    [int]$X,
    [int]$Y,
    [int]$Width,
    [int]$Height,
    [string[]]$Palette,
    [int]$Seed
) {
    $shadow = C $Palette[0]
    $low = C $Palette[1]
    $mid = C $Palette[2]
    $light = C $Palette[3]
    $accent = C $Palette[4]

    for ($py = $Y; $py -lt ($Y + $Height); $py++) {
        for ($px = $X; $px -lt ($X + $Width); $px++) {
            Set-Pixel $Bitmap $px $py $mid
        }
    }

    # Mirrored broad clusters keep both sides intentional, but their positions are
    # scattered rather than aligned to rows.  This is crucial: a regular grid of
    # little marks reads as brickwork once wrapped around a creature.
    $clusterCount = [Math]::Max(2, [int](($Width * $Height) / 600))
    $halfWidth = [Math]::Max(1, [int][Math]::Ceiling($Width / 2.0))
    for ($index = 0; $index -lt $clusterCount; $index++) {
        $hash = Get-ArtHash ($index + $X) ($Seed + $index * 17) ($Seed + $Y)
        $clusterWidth = [Math]::Min(10, [Math]::Max(3, 4 + ($hash % 7)))
        $clusterHeight = [Math]::Min(8, [Math]::Max(3, 3 + (($hash -shr 4) % 6)))
        $usableHalf = [Math]::Max(1, $halfWidth - $clusterWidth)
        $usableHeight = [Math]::Max(1, $Height - $clusterHeight)
        $gx = $X + (($hash -shr 8) % $usableHalf)
        $gy = $Y + (($hash -shr 13) % $usableHeight)
        $tone = if (($hash % 11) -eq 0) { $accent }
            elseif (($hash % 4) -eq 0) { $light }
            elseif (($hash % 3) -eq 0) { $shadow }
            else { $low }

        for ($dy = 0; $dy -lt $clusterHeight; $dy++) {
            for ($dx = 0; $dx -lt $clusterWidth; $dx++) {
                # Chamfer alternating corners to produce an organic Minecraft blob.
                if ((($dx -eq 0 -or $dx -eq $clusterWidth - 1) -and
                     ($dy -eq 0 -or $dy -eq $clusterHeight - 1)) -or
                    ((($dx + $dy + $hash) % 9) -eq 0)) { continue }
                $leftX = $gx + $dx
                $rightX = $X + $Width - 1 - ($leftX - $X)
                Set-Pixel $Bitmap $leftX ($gy + $dy) $tone
                Set-Pixel $Bitmap $rightX ($gy + $dy) $tone
            }
        }
    }

    # Short broken edge accents give selected faces volume without drawing a full
    # horizontal rail around every UV rectangle.
    $edgeLength = [Math]::Min([Math]::Max(2, [int]($Width / 3)), 10)
    $edgeOffset = (Get-ArtHash $Width $Height $Seed) % [Math]::Max(1, ($Width - $edgeLength + 1))
    for ($px = 0; $px -lt $edgeLength; $px++) {
        Set-Pixel $Bitmap ($X + $edgeOffset + $px) $Y $light
        Set-Pixel $Bitmap ($X + $Width - 1 - $edgeOffset - $px) ($Y + $Height - 1) $shadow
    }
}

function Fill-UvIsland(
    $Bitmap,
    [int]$X,
    [int]$Y,
    [int]$Width,
    [int]$Height,
    [string[]]$Palette,
    [int]$Seed
) {
    $colors = @($Palette | ForEach-Object { C $_ })
    for ($py = 0; $py -lt $Height; $py++) {
        for ($px = 0; $px -lt $Width; $px++) {
            $hash = Get-ArtHash ([int](($X + $px) / 2)) ([int](($Y + $py) / 2)) $Seed
            $tone = if (($hash % 11) -eq 0) { 4 }
                elseif (($hash % 7) -eq 0) { 0 }
                elseif (($hash % 3) -eq 0) { 1 }
                elseif (($hash % 4) -eq 0) { 3 }
                else { 2 }
            Set-Pixel $Bitmap ($X + $px) ($Y + $py) $colors[$tone]
        }
    }
    if ($Width -gt 1 -and $Height -gt 1) {
        Set-Pixel $Bitmap $X $Y $colors[3]
        Set-Pixel $Bitmap ($X + $Width - 1) ($Y + $Height - 1) $colors[0]
    }
}

function New-CreatureAtlas(
    [string]$Name,
    [string[]]$Hide,
    [string[]]$Bone,
    [string[]]$Tissue,
    [string[]]$Accent,
    [int]$FaceX,
    [int]$FaceY,
    [int]$FaceW,
    [int]$FaceH,
    [int]$Seed
) {
    $bitmap = New-Object System.Drawing.Bitmap(128, 128, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        Fill-Region $bitmap 0 0 128 128 $Hide $Seed

        # The established atlas layout is divided into large material families.  The
        # exact model UV islands remain in bounds and now receive textured footprints.
        Fill-Region $bitmap 0 0 32 16 $Hide ($Seed + 1)
        Fill-Region $bitmap 96 0 32 16 $Hide ($Seed + 1)
        Fill-Region $bitmap 32 16 64 16 $Hide ($Seed + 2)
        Fill-Region $bitmap 0 32 24 18 $Tissue ($Seed + 3)
        Fill-Region $bitmap 104 32 24 18 $Tissue ($Seed + 3)
        Fill-Region $bitmap 32 48 32 20 $Hide ($Seed + 4)
        Fill-Region $bitmap 64 48 32 20 $Hide ($Seed + 4)
        Fill-Region $bitmap 40 68 48 18 $Bone ($Seed + 5)
        Fill-Region $bitmap 24 86 32 20 $Tissue ($Seed + 6)
        Fill-Region $bitmap 72 86 32 20 $Tissue ($Seed + 6)
        Fill-Region $bitmap 0 106 40 22 $Bone ($Seed + 7)
        Fill-Region $bitmap 88 106 40 22 $Bone ($Seed + 7)
        Fill-Region $bitmap 40 106 48 22 $Accent ($Seed + 8)

        if ($Name -eq 'buried_remnant') {
            # Exact post-rig UV islands. Bilateral pairs share these footprints by
            # design; the material split makes cage/bone, soil clods and root
            # strands readable instead of inheriting a flat atlas underlay.
            $remnantIslands = @(
                @(64,64,4,6,'bone'), @(70,64,14,2,'bone'),
                @(86,64,6,10,'bone'), @(92,64,12,8,'bone'),
                @(0,72,28,9,'hide'), @(28,72,20,11,'hide'),
                @(50,72,4,6,'accent'), @(60,72,20,11,'hide'),
                @(82,72,24,10,'bone')
            )
            foreach ($island in $remnantIslands) {
                $material = switch ($island[4]) {
                    'bone' { $Bone }
                    'accent' { $Accent }
                    default { $Hide }
                }
                Fill-UvIsland $bitmap $island[0] $island[1] $island[2] $island[3] $material ($Seed + 40 + $island[0])
            }
            foreach ($island in $remnantIslands) {
                $colors = [System.Collections.Generic.HashSet[int]]::new()
                for ($py = 0; $py -lt $island[3]; $py++) {
                    for ($px = 0; $px -lt $island[2]; $px++) {
                        [void]$colors.Add($bitmap.GetPixel($island[0] + $px, $island[1] + $py).ToArgb())
                    }
                }
                if ($colors.Count -lt 2) { throw "buried_remnant UV island $($island[0]),$($island[1]) is flat" }
            }
        }

        # A symmetric, readable face plane: eyes are cavities, not neon dots.
        Fill-Region $bitmap $FaceX $FaceY $FaceW $FaceH $Bone ($Seed + 9)
        $eyeY = $FaceY + [Math]::Max(2, [int]($FaceH / 3))
        $eyeInset = [Math]::Max(1, [int]($FaceW / 4))
        $leftEye = $FaceX + $eyeInset
        $rightEye = $FaceX + $FaceW - 1 - $eyeInset
        foreach ($dy in 0..1) {
            Set-Pixel $bitmap $leftEye ($eyeY + $dy) (C $Hide[0])
            Set-Pixel $bitmap $rightEye ($eyeY + $dy) (C $Hide[0])
        }
        if ($FaceW -ge 6 -and $FaceH -ge 5) {
            $mouthX = $FaceX + [int]($FaceW / 2) - 1
            Set-Pixel $bitmap $mouthX ($FaceY + $FaceH - 2) (C $Tissue[1])
            Set-Pixel $bitmap ($mouthX + 1) ($FaceY + $FaceH - 2) (C $Tissue[1])
        }

        $path = Join-Path $script:ProjectRoot "src\main\resources\assets\gravesown\textures\entity\$Name.png"
        $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Host "Created $Name.png"
    }
    finally {
        $bitmap.Dispose()
    }
}

$bone = @('#685E49','#8C8063','#B1A57F','#D2C79D','#E3D8AC')
$tissue = @('#3C2022','#6A3032','#8E4242','#B45D56','#D17A68')
$olive = @('#263322','#3D4E2F','#58683E','#788A55','#9AA66C')

New-CreatureAtlas 'hollow_grazer' @('#2C2924','#49382D','#6A5140','#8B6B53','#A57C59') $bone $tissue $olive 7 7 7 7 101
New-CreatureAtlas 'ribspring' @('#332D25','#554534','#796249','#9C7B59','#B48D62') $bone $tissue $olive 7 7 6 5 131
New-CreatureAtlas 'stitchtusk' @('#292421','#47352C','#62483A','#82604A','#9A704F') $bone $tissue $olive 10 10 10 9 163
New-CreatureAtlas 'woundscent' @('#252824','#3D4036','#595A48','#77765D','#8E8464') $bone $tissue $olive 7 7 8 6 197
New-CreatureAtlas 'buried_remnant' @('#2C2D27','#4A4A3C','#696653','#89836A','#A29A78') $bone $tissue $olive 8 8 8 8 223

Write-Host 'PASS generated five textured, symmetric Texture Language V2 creature atlases.' -ForegroundColor Green
