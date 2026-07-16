Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

function Color([string]$hex) { [System.Drawing.ColorTranslator]::FromHtml($hex) }
function Color-A([int]$alpha, [string]$hex) {
    $base = Color $hex
    [System.Drawing.Color]::FromArgb($alpha, $base.R, $base.G, $base.B)
}
function Brush([string]$hex) { New-Object System.Drawing.SolidBrush (Color $hex) }
function Brush-A([int]$alpha, [string]$hex) { New-Object System.Drawing.SolidBrush (Color-A $alpha $hex) }
function Fill($g, $b, [int]$x, [int]$y, [int]$w, [int]$h) { $g.FillRectangle($b, $x, $y, $w, $h) }
function Save($bitmap, [string]$relative) {
    $path = Join-Path $script:ProjectRoot $relative
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Created $relative"
}

function Art-Hash([int]$x, [int]$y, [int]$seed) {
    [int64]$value = ([int64]$x * 73856093) -bxor ([int64]$y * 19349663) -bxor ([int64]$seed * 83492791)
    $value = ($value -bxor ($value -shr 11)) % 2147483647
    $value = (($value * 48271) + 12345) % 2147483647
    if ($value -lt 0) { $value = -$value }
    [int]$value
}

function Paint-MottledUnderlay($bitmap, [string]$baseHex, [string]$lowHex, [string]$lightHex, [int]$seed) {
    $base = Color $baseHex
    $low = Color $lowHex
    $light = Color $lightHex
    for ($y = 0; $y -lt $bitmap.Height; $y++) {
        for ($x = 0; $x -lt $bitmap.Width; $x++) { $bitmap.SetPixel($x, $y, $base) }
    }
    $count = [Math]::Max(8, [int](($bitmap.Width * $bitmap.Height) / 320))
    for ($index = 0; $index -lt $count; $index++) {
        $hash = Art-Hash $index ($seed + $index * 13) $seed
        $x0 = ($hash -shr 3) % [Math]::Max(1, ($bitmap.Width - 7))
        $y0 = ($hash -shr 11) % [Math]::Max(1, ($bitmap.Height - 6))
        $w = 3 + ($hash % 5)
        $h = 2 + (($hash -shr 6) % 4)
        $color = if (($index % 4) -eq 0) { $light } else { $low }
        for ($dy = 0; $dy -lt $h; $dy++) {
            for ($dx = 0; $dx -lt $w; $dx++) {
                if ((($dx -eq 0 -or $dx -eq $w - 1) -and ($dy -eq 0 -or $dy -eq $h - 1))) { continue }
                if (($x0 + $dx) -lt $bitmap.Width -and ($y0 + $dy) -lt $bitmap.Height) {
                    $bitmap.SetPixel($x0 + $dx, $y0 + $dy, $color)
                }
            }
        }
    }
}

function Paint-UvIsland(
    [System.Drawing.Bitmap]$bitmap,
    [int]$x,
    [int]$y,
    [int]$width,
    [int]$height,
    [string[]]$palette,
    [int]$seed
) {
    $colors = @($palette | ForEach-Object { Color $_ })
    for ($py = 0; $py -lt $height; $py++) {
        for ($px = 0; $px -lt $width; $px++) {
            # Two-pixel clusters keep the atlas Minecraft-readable while every
            # small UV island still receives more than one value.
            $hash = Art-Hash ([int](($x + $px) / 2)) ([int](($y + $py) / 2)) $seed
            $tone = if (($hash % 17) -eq 0) { 4 }
                elseif (($hash % 7) -eq 0) { 0 }
                elseif (($hash % 5) -eq 0) { 1 }
                elseif (($hash % 3) -eq 0) { 3 }
                else { 2 }
            if (($x + $px) -lt $bitmap.Width -and ($y + $py) -lt $bitmap.Height) {
                $bitmap.SetPixel($x + $px, $y + $py, $colors[$tone])
            }
        }
    }
    if ($width -gt 1 -and $height -gt 1) {
        $bitmap.SetPixel($x, $y, $colors[3])
        $bitmap.SetPixel($x + $width - 1, $y + $height - 1, $colors[0])
    }
}

function New-FishAtlasV3(
    [string]$name,
    [string[]]$underlay,
    [hashtable]$palettes,
    [object[]]$islands,
    [int]$seed
) {
    $bitmap = New-Object System.Drawing.Bitmap(128, 128, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        Paint-MottledUnderlay $bitmap $underlay[0] $underlay[1] $underlay[2] $seed
        for ($index = 0; $index -lt $islands.Count; $index++) {
            $island = $islands[$index]
            Paint-UvIsland $bitmap $island[0] $island[1] $island[2] $island[3] $palettes[$island[4]] ($seed + 17 + $index * 11)
        }

        $errors = [System.Collections.Generic.List[string]]::new()
        foreach ($island in $islands) {
            $colors = [System.Collections.Generic.HashSet[int]]::new()
            for ($py = 0; $py -lt $island[3]; $py++) {
                for ($px = 0; $px -lt $island[2]; $px++) {
                    [void]$colors.Add($bitmap.GetPixel($island[0] + $px, $island[1] + $py).ToArgb())
                }
            }
            if (($island[2] * $island[3]) -gt 1 -and $colors.Count -lt 2) {
                $errors.Add("$name UV island $($island[0]),$($island[1]) is flat")
            }
        }
        if ($errors.Count -gt 0) { throw "Fish UV audit failed:`n$($errors -join "`n")" }

        Save $bitmap "src/main/resources/assets/gravesown/textures/entity/$name.png"
    }
    finally { $bitmap.Dispose() }
}

$p = @{
    abyss = Brush '#152826'; deep = Brush '#24443D'; water = Brush '#2F6B60'
    waterLight = Brush '#5A9A7E'; film = Brush '#9CAF61'; rust = Brush '#9B5142'
    flesh = Brush '#8B4542'; fleshLight = Brush '#C06A5C'; bone = Brush '#C0B487'
    boneDark = Brush '#82775B'; black = Brush '#202624'; metal = Brush '#596361'
    metalLight = Brush '#8D9A92'; kelp = Brush '#3F713D'; kelpLight = Brush '#78A653'
}

try {
    # Six coherent hard-pixel frames. Each frame moves one shared wave field by
    # a single pixel, avoiding the flashing caused by unrelated frames.
    $waterDeepColor = Color-A 198 '#0D1C19'
    $waterBodyColor = Color-A 208 '#18332C'
    $waterShadeColor = Color-A 190 '#0A1514'
    $waterLightColor = Color-A 218 '#315743'
    $waterFilm = Color-A 224 '#69794F'
    $waterRustColor = Color-A 205 '#6A3A39'
    $still = New-Object System.Drawing.Bitmap(16, 96, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($still)
    try {
        for ($frame = 0; $frame -lt 6; $frame++) {
            $oy = $frame * 16
            for ($y = 0; $y -lt 16; $y++) {
                for ($x = 0; $x -lt 16; $x++) {
                    $wave = ($x + [int]($y / 2) - $frame + 64) % 16
                    $color = if ($wave -lt 6) { $waterBodyColor }
                        elseif ($wave -eq 7 -or $wave -eq 8) { $waterLightColor }
                        elseif ($wave -gt 12) { $waterShadeColor }
                        else { $waterDeepColor }
                    $still.SetPixel($x, $oy + $y, $color)
                }
            }
            foreach ($point in @(@(2,3), @(8,9), @(13,5), @(5,14))) {
                $x = ($point[0] + $frame) % 16
                $still.SetPixel($x, $oy + $point[1], $waterFilm)
            }
            $still.SetPixel((13 + $frame) % 16, $oy + 12, $waterRustColor)
        }
    } finally { $g.Dispose() }
    Save $still 'src/main/resources/assets/gravesown/textures/block/gloamwater_still.png'
    $still.Dispose()

    $flow = New-Object System.Drawing.Bitmap(32, 192, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($flow)
    try {
        for ($frame = 0; $frame -lt 6; $frame++) {
            $oy = $frame * 32
            for ($y = 0; $y -lt 32; $y++) {
                for ($x = 0; $x -lt 32; $x++) {
                    $wave = ($x - $y - $frame + 128) % 18
                    $color = if ($wave -lt 7) { $waterBodyColor }
                        elseif ($wave -eq 7) { $waterLightColor }
                        elseif ($wave -gt 14) { $waterShadeColor }
                        else { $waterDeepColor }
                    $flow.SetPixel($x, $oy + $y, $color)
                }
            }
            for ($mark = 0; $mark -lt 5; $mark++) {
                $x = ($mark * 7 + $frame) % 32
                $y = ($mark * 11 + 4) % 32
                $flow.SetPixel($x, $oy + $y, $waterFilm)
            }
        }
    } finally { $g.Dispose() }
    Save $flow 'src/main/resources/assets/gravesown/textures/block/gloamwater_flow.png'
    $flow.Dispose()

    $animationMetadata = @'
{
  "animation": {
    "frametime": 6,
    "interpolate": false
  }
}
'@
    foreach ($texture in @('gloamwater_still.png', 'gloamwater_flow.png')) {
        $metadataPath = Join-Path $script:ProjectRoot "src/main/resources/assets/gravesown/textures/block/$texture.mcmeta"
        [System.IO.File]::WriteAllText($metadataPath, $animationMetadata, (New-Object System.Text.UTF8Encoding($false)))
        Write-Host "Created src/main/resources/assets/gravesown/textures/block/$texture.mcmeta"
    }

    $kelp = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($kelp)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        Fill $g $p.kelp 7 4 2 12
        Fill $g $p.kelpLight 8 5 1 9
        Fill $g $p.kelp 4 8 2 8
        Fill $g $p.kelpLight 5 9 1 6
        Fill $g $p.kelp 11 6 2 10
        Fill $g $p.film 12 8 1 6
        Fill $g $p.rust 3 11 3 2
        Fill $g $p.rust 9 9 4 2
        Fill $g $p.boneDark 6 14 5 2
        $kelp.SetPixel(3, 10, (Color '#75824D'))
        $kelp.SetPixel(13, 5, (Color '#64764A'))
    } finally { $g.Dispose() }
    Save $kelp 'src/main/resources/assets/gravesown/textures/block/threadkelp.png'
    $kelp.Dispose()

    # Model geometry owns these authoritative 128px islands.  Mirrored bilateral
    # parts intentionally share coordinates; every distinct footprint receives a
    # clustered material treatment and is audited against a flat/empty island.
    $rotfinPalettes = @{
        hide=@('#26352F','#384A40','#526456','#70816A','#8E976F')
        belly=@('#4B302D','#6E3C38','#91504A','#B86B5C','#D28A70')
        fin=@('#263B36','#39564B','#527160','#789276','#A5AE80')
        bone=@('#5A5648','#7C745C','#A39A76','#C5BA8E','#DED2A4')
    }
    $rotfinUv = @(
        @(0,0,38,15,'hide'), @(0,16,30,9,'belly'), @(32,16,24,8,'bone'),
        @(40,0,30,11,'hide'), @(72,0,22,7,'hide'), @(96,0,20,4,'bone'),
        @(72,10,22,6,'belly'), @(96,10,6,4,'bone'), @(104,10,6,4,'bone'),
        @(110,10,10,8,'belly'), @(122,10,4,2,'bone'),
        @(96,30,12,6,'fin'), @(110,30,10,5,'fin'), @(54,44,14,10,'bone'),
        @(0,30,14,9,'fin'), @(16,30,10,7,'fin'), @(28,30,8,5,'fin'),
        @(40,30,20,6,'fin'), @(62,30,16,5,'fin'), @(80,30,14,5,'fin'),
        @(0,44,20,10,'hide'), @(22,44,16,8,'hide'), @(40,44,12,14,'fin')
    )
    New-FishAtlasV3 'rotfin' @('#33473D','#22342E','#64755E') $rotfinPalettes $rotfinUv 307

    $veilfinPalettes = @{
        hide=@('#2C4936','#3F6445','#5D8055','#7FA267','#A8B878')
        belly=@('#516047','#6E7B55','#8E9968','#B1B480','#D0C99B')
        fin=@('#29473B','#3C6750','#568565','#78A27A','#A2BE8D')
        bone=@('#5C604D','#7C8163','#A0A47D','#C3C596','#DEDAB2')
    }
    $veilfinUv = @(
        @(0,0,34,14,'hide'), @(0,16,26,9,'belly'), @(30,16,20,7,'bone'),
        @(40,0,24,9,'hide'), @(66,0,16,5,'hide'), @(84,0,18,5,'bone'),
        @(66,8,16,5,'belly'), @(84,8,8,6,'belly'), @(94,8,4,2,'bone'),
        @(0,30,20,6,'fin'), @(22,30,16,5,'fin'), @(40,30,14,5,'fin'),
        @(56,30,14,10,'fin'), @(72,30,10,8,'fin'), @(72,44,12,8,'bone'),
        @(0,44,18,9,'hide'), @(20,44,16,8,'hide'), @(38,44,10,12,'fin')
    )
    New-FishAtlasV3 'veilfin' @('#426847','#2B4934','#78935C') $veilfinPalettes $veilfinUv 337

    $rootskimmerPalettes = @{
        hide=@('#443F32','#5E5741','#7B7254','#9B8F68','#B7A778')
        belly=@('#514039','#705249','#92675A','#B17C67','#CF9A7C')
        fin=@('#33463D','#485F50','#627968','#82947A','#A8AD89')
        bone=@('#615D4D','#817B61','#A39B77','#C5B98E','#DDD0A3')
    }
    $rootskimmerUv = @(
        @(0,0,42,15,'hide'), @(0,16,34,10,'belly'), @(36,16,26,8,'bone'),
        @(44,0,27,9,'hide'), @(72,0,16,5,'hide'), @(90,0,20,5,'bone'),
        @(66,8,16,5,'belly'), @(84,8,8,6,'belly'), @(94,8,4,2,'bone'),
        @(60,44,12,6,'fin'), @(0,30,18,6,'fin'), @(22,30,16,5,'fin'),
        @(40,30,14,5,'fin'), @(56,30,14,10,'fin'), @(72,30,10,8,'fin'),
        @(72,44,12,8,'bone'), @(0,44,18,9,'hide'), @(20,44,16,8,'hide'),
        @(38,44,10,12,'fin')
    )
    New-FishAtlasV3 'rootskimmer' @('#6A6049','#403B31','#9B8D68') $rootskimmerPalettes $rootskimmerUv 359

    $bucket = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($bucket)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        Fill $g $p.metal 3 4 10 10
        Fill $g $p.metalLight 4 5 8 2
        Fill $g $p.black 4 3 8 2
        Fill $g $p.water 4 6 8 6
        Fill $g $p.waterLight 5 6 6 1
        Fill $g $p.rust 6 10 5 1
        Fill $g $p.metalLight 2 4 1 7
        Fill $g $p.metalLight 13 4 1 7
        Fill $g $p.black 5 14 6 1
    } finally { $g.Dispose() }
    Save $bucket 'src/main/resources/assets/gravesown/textures/item/gloamwater_bucket.png'
    $bucket.Dispose()

    $meat = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($meat)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        Fill $g $p.black 3 4 10 8
        Fill $g $p.flesh 2 5 11 6
        Fill $g $p.fleshLight 4 5 7 2
        Fill $g $p.bone 10 7 4 2
        Fill $g $p.rust 5 9 6 2
        Fill $g $p.kelp 3 8 2 2
    } finally { $g.Dispose() }
    Save $meat 'src/main/resources/assets/gravesown/textures/item/rotfin_flesh.png'
    $meat.Dispose()

    foreach ($dropName in @('veilfin_fillet','rootskimmer_meat')) {
        $drop = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g = [System.Drawing.Graphics]::FromImage($drop)
        try {
            $g.Clear([System.Drawing.Color]::Transparent)
            Fill $g $p.black 2 5 12 7
            if ($dropName -eq 'veilfin_fillet') {
                Fill $g $p.kelp 3 4 10 7
                Fill $g $p.kelpLight 4 5 8 2
                Fill $g $p.film 6 8 6 2
            }
            else {
                Fill $g $p.flesh 3 4 10 7
                Fill $g $p.fleshLight 4 5 8 2
                Fill $g $p.bone 10 8 3 2
            }
            Fill $g $p.rust 4 10 6 1
        } finally { $g.Dispose() }
        Save $drop "src/main/resources/assets/gravesown/textures/item/$dropName.png"
        $drop.Dispose()
    }

    $silt = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($silt)
    try {
        Fill $g $p.abyss 0 0 16 16
        for ($y = 0; $y -lt 16; $y += 3) {
            Fill $g $p.deep (($y * 5) % 13) $y 5 2
            $silt.SetPixel((($y * 7 + 3) % 16), (($y + 1) % 16), (Color '#2E5140'))
        }
        foreach ($point in @(@(2,4),@(11,2),@(7,8),@(14,12),@(4,14))) {
            $silt.SetPixel($point[0], $point[1], (Color '#713C3B'))
        }
    } finally { $g.Dispose() }
    Save $silt 'src/main/resources/assets/gravesown/textures/block/abyssal_silt.png'
    $silt.Dispose()

    $brine = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($brine)
    try {
        Fill $g $p.boneDark 0 0 16 16
        Fill $g $p.bone 1 1 6 4
        Fill $g $p.metalLight 9 2 5 5
        Fill $g $p.deep 6 5 4 3
        Fill $g $p.bone 2 10 5 4
        Fill $g $p.bone 10 9 4 5
        Fill $g $p.black 0 7 5 2
        Fill $g $p.rust 7 13 3 2
        foreach ($point in @(@(3,3),@(12,4),@(8,7),@(4,12),@(13,11))) {
            $brine.SetPixel($point[0], $point[1], (Color '#D0C59D'))
        }
    } finally { $g.Dispose() }
    Save $brine 'src/main/resources/assets/gravesown/textures/block/brinebone.png'
    $brine.Dispose()

    foreach ($plantName in @('veilweed','drowned_roots','bladderpod','lumen_kelp')) {
        $plant = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g = [System.Drawing.Graphics]::FromImage($plant)
        try {
            $g.Clear([System.Drawing.Color]::Transparent)
            if ($plantName -eq 'veilweed') {
                Fill $g $p.kelp 7 2 2 14
                Fill $g $p.kelpLight 8 3 1 11
                Fill $g $p.kelp 3 6 5 2
                Fill $g $p.film 4 5 3 1
                Fill $g $p.kelp 8 9 6 2
                Fill $g $p.rust 11 8 2 2
                Fill $g $p.kelp 5 12 3 2
            }
            elseif ($plantName -eq 'drowned_roots') {
                Fill $g $p.boneDark 6 1 3 15
                Fill $g $p.deep 8 2 2 12
                Fill $g $p.boneDark 2 5 5 2
                Fill $g $p.bone 3 4 2 2
                Fill $g $p.boneDark 9 8 5 2
                Fill $g $p.rust 11 7 2 2
                Fill $g $p.deep 3 12 4 3
                Fill $g $p.bone 12 11 2 4
            }
            elseif ($plantName -eq 'bladderpod') {
                Fill $g $p.kelp 7 5 2 11
                Fill $g $p.kelpLight 8 6 1 8
                Fill $g $p.deep 3 2 5 5
                Fill $g $p.film 4 1 3 5
                Fill $g $p.rust 5 3 2 2
                Fill $g $p.deep 9 7 5 5
                Fill $g $p.film 10 6 3 5
                Fill $g $p.bone 11 8 1 2
            }
            else {
                Fill $g $p.kelp 7 2 2 14
                Fill $g $p.kelpLight 8 3 1 11
                Fill $g $p.film 4 5 4 3
                Fill $g $p.bone 5 6 2 1
                Fill $g $p.film 9 9 5 3
                Fill $g $p.bone 11 10 2 1
                Fill $g $p.kelp 4 12 3 2
                $plant.SetPixel(5, 5, (Color '#D8D78A'))
                $plant.SetPixel(12, 9, (Color '#D8D78A'))
            }
        } finally { $g.Dispose() }
        Save $plant "src/main/resources/assets/gravesown/textures/block/$plantName.png"
        $plant.Dispose()
    }

    $skiff = New-Object System.Drawing.Bitmap(128, 64, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($skiff)
    try {
        Fill $g $p.black 0 0 128 64
        for ($y = 0; $y -lt 64; $y += 8) {
            Fill $g $p.deep (($y * 3) % 107) $y 21 4
            Fill $g $p.boneDark (($y * 5 + 19) % 113) ($y + 4) 15 2
        }
        Fill $g $p.boneDark 0 0 96 20
        Fill $g $p.bone 2 2 92 3
        Fill $g $p.deep 5 7 84 9
        Fill $g $p.rust 12 12 68 3
        Fill $g $p.kelp 0 22 118 17
        Fill $g $p.kelpLight 3 24 111 3
        Fill $g $p.deep 8 30 103 7
        Fill $g $p.boneDark 0 41 128 16
        Fill $g $p.bone 5 43 118 3
        Fill $g $p.rust 14 50 97 4
        foreach ($x in 7,19,34,51,72,95,116) {
            Fill $g $p.black $x 0 3 58
            Fill $g $p.metalLight ($x + 1) 8 1 38
        }
    } finally { $g.Dispose() }
    Save $skiff 'src/main/resources/assets/gravesown/textures/entity/gloam_skiff.png'
    $skiff.Dispose()

    $skiffItem = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($skiffItem)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        Fill $g $p.black 2 6 12 7
        Fill $g $p.kelp 1 7 14 4
        Fill $g $p.kelpLight 3 7 10 1
        Fill $g $p.boneDark 3 11 10 2
        Fill $g $p.bone 5 5 1 7
        Fill $g $p.bone 10 5 1 7
        Fill $g $p.rust 6 9 4 1
    } finally { $g.Dispose() }
    Save $skiffItem 'src/main/resources/assets/gravesown/textures/item/gloam_skiff.png'
    $skiffItem.Dispose()
}
finally {
    foreach ($brush in $p.Values) { $brush.Dispose() }
}
