Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

# Deterministic 128x128 hard-edged atlases for the native ecosystem roster.
# Large mirrored clusters keep bilateral animals coherent; species-specific bands,
# saddles and accents prevent the shared animation rig from reading as palette swaps.
function C([string]$Hex) { [System.Drawing.ColorTranslator]::FromHtml($Hex) }

function Fill($Graphics, [string]$Hex, [int]$X, [int]$Y, [int]$W, [int]$H) {
    $brush = [System.Drawing.SolidBrush]::new((C $Hex))
    try { $Graphics.FillRectangle($brush, $X, $Y, $W, $H) }
    finally { $brush.Dispose() }
}

function Get-FaunaHash([string]$Text, [int]$Seed) {
    [int64]$value = $Seed
    foreach ($character in $Text.ToCharArray()) {
        $value = (($value * 131) + [int]$character) % 2147483629
    }
    [int]$value
}

function New-NativeAtlas([string]$Id, [hashtable]$Palette, [string]$Pattern) {
    $bitmap = [System.Drawing.Bitmap]::new(128, 128, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
        Fill $graphics $Palette.Base 0 0 128 128

        $seed = Get-FaunaHash $Id 7331
        # Broad, mirrored body clusters. No one-pixel noise or repeating brick rails.
        for ($index = 0; $index -lt 22; $index++) {
            $hash = Get-FaunaHash "$Id/$index" ($seed + $index * 97)
            $w = 4 + ($hash % 9)
            $h = 3 + (($hash -shr 3) % 8)
            $x = 3 + (($hash -shr 7) % [Math]::Max(1, (59 - $w)))
            $y = 3 + (($hash -shr 13) % [Math]::Max(1, (121 - $h)))
            $tone = if (($index % 5) -eq 0) { $Palette.Light }
                elseif (($index % 3) -eq 0) { $Palette.Accent }
                else { $Palette.Shadow }
            Fill $graphics $tone $x $y $w $h
            Fill $graphics $tone (128 - $x - $w) $y $w $h
        }

        switch ($Pattern) {
            'bands' {
                foreach ($y in 18, 44, 72, 101) {
                    Fill $graphics $Palette.Accent 8 $y 112 4
                    Fill $graphics $Palette.Light 24 ($y + 4) 80 2
                }
            }
            'saddle' {
                Fill $graphics $Palette.Shadow 28 30 72 34
                Fill $graphics $Palette.Accent 38 36 52 20
                Fill $graphics $Palette.Light 48 38 32 5
            }
            'mask' {
                Fill $graphics $Palette.Shadow 4 4 46 24
                Fill $graphics $Palette.Shadow 78 4 46 24
                Fill $graphics $Palette.Accent 12 10 12 5
                Fill $graphics $Palette.Accent 104 10 12 5
            }
            'plates' {
                foreach ($x in 8, 31, 54, 77, 100) {
                    Fill $graphics $Palette.Shadow $x 28 18 36
                    Fill $graphics $Palette.Light ($x + 3) 31 12 5
                }
            }
            'spots' {
                foreach ($point in @(@(14,24),@(38,51),@(18,86),@(47,106))) {
                    Fill $graphics $Palette.Accent $point[0] $point[1] 8 7
                    Fill $graphics $Palette.Accent (120 - $point[0]) $point[1] 8 7
                }
            }
            'feather' {
                foreach ($y in 28, 40, 52, 64, 76) {
                    $offset = (($y / 4) % 2) * 5
                    for ($x = 8 + $offset; $x -lt 120; $x += 18) {
                        Fill $graphics $Palette.Shadow $x $y 10 5
                        Fill $graphics $Palette.Light ($x + 2) ($y + 1) 6 2
                    }
                }
            }
            'ray' {
                for ($ring = 0; $ring -lt 4; $ring++) {
                    Fill $graphics $Palette.Shadow (8 + $ring * 12) (22 + $ring * 12) (112 - $ring * 24) 4
                    Fill $graphics $Palette.Light (14 + $ring * 12) (27 + $ring * 12) (100 - $ring * 24) 2
                }
            }
        }

        # UV-local face/limb contrast: restrained and mirrored.
        Fill $graphics $Palette.Dark 6 7 7 5
        Fill $graphics $Palette.Dark 115 7 7 5
        Fill $graphics $Palette.Eye 14 15 4 4
        Fill $graphics $Palette.Eye 110 15 4 4
        Fill $graphics $Palette.Light 2 66 12 3
        Fill $graphics $Palette.Light 114 66 12 3

        $path = Join-Path $script:ProjectRoot "src\main\resources\assets\gravesown\textures\entity\$Id.png"
        $directory = Split-Path -Parent $path
        New-Item -ItemType Directory -Force -Path $directory | Out-Null
        $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

$species = [ordered]@{
    ash_hopper  = @{ Palette=@{Base='#8b8068';Shadow='#514b42';Light='#b9ad8b';Accent='#6c7160';Dark='#292a27';Eye='#d6b45e'}; Pattern='spots' }
    gravewing   = @{ Palette=@{Base='#4c5360';Shadow='#282d36';Light='#89919c';Accent='#6e5548';Dark='#171a20';Eye='#d8c47a'}; Pattern='feather' }
    rootback    = @{ Palette=@{Base='#52604b';Shadow='#2f3a2e';Light='#839172';Accent='#766447';Dark='#20261f';Eye='#c8b65d'}; Pattern='plates' }
    bark_marten = @{ Palette=@{Base='#754f39';Shadow='#3c2c25';Light='#a9825f';Accent='#c1a06d';Dark='#211b18';Eye='#e1c86e'}; Pattern='mask' }
    crag_ram    = @{ Palette=@{Base='#6f6b66';Shadow='#393b3c';Light='#a7a29a';Accent='#8a7355';Dark='#242526';Eye='#d9bc63'}; Pattern='saddle' }
    rift_puma   = @{ Palette=@{Base='#765c58';Shadow='#3b3133';Light='#a28279';Accent='#5e454a';Dark='#211c20';Eye='#e2c06b'}; Pattern='spots' }
    mire_toad   = @{ Palette=@{Base='#5f7058';Shadow='#344331';Light='#8ea57c';Accent='#8a654d';Dark='#20291f';Eye='#d8c25f'}; Pattern='spots' }
    reed_lynx   = @{ Palette=@{Base='#827457';Shadow='#494033';Light='#b3a47b';Accent='#59634f';Dark='#27241f';Eye='#d9bc55'}; Pattern='mask' }
    silt_ray    = @{ Palette=@{Base='#45656a';Shadow='#243d43';Light='#77969a';Accent='#657a63';Dark='#18272b';Eye='#c5d17a'}; Pattern='ray' }
    ember_fox   = @{ Palette=@{Base='#9b5a36';Shadow='#523124';Light='#c98250';Accent='#d0a15e';Dark='#2c1e19';Eye='#f0d16a'}; Pattern='mask' }
    cinder_fowl = @{ Palette=@{Base='#78483c';Shadow='#3d2928';Light='#aa7260';Accent='#c68a43';Dark='#241b1c';Eye='#e7ce69'}; Pattern='feather' }
    pallid_hart = @{ Palette=@{Base='#a19a84';Shadow='#5c5b51';Light='#cdc5a9';Accent='#778476';Dark='#30312e';Eye='#d8bd63'}; Pattern='saddle' }
    mossboar    = @{ Palette=@{Base='#59654d';Shadow='#303a2d';Light='#899676';Accent='#6f513c';Dark='#20251e';Eye='#d7b75e'}; Pattern='bands' }
    amber_jay   = @{ Palette=@{Base='#b77d3c';Shadow='#5e452b';Light='#dfaa5c';Accent='#56777a';Dark='#2b2822';Eye='#f0dc7a'}; Pattern='feather' }
    sunhorn     = @{ Palette=@{Base='#b08b53';Shadow='#604b32';Light='#dec17f';Accent='#8b6840';Dark='#30271d';Eye='#efe08a'}; Pattern='bands' }
}

foreach ($entry in $species.GetEnumerator()) {
    New-NativeAtlas $entry.Key $entry.Value.Palette $entry.Value.Pattern
}

Write-Host "PASS native fauna art: $($species.Count) deterministic 128x128 atlases" -ForegroundColor Green
