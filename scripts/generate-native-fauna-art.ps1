Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

# Deterministic, UV-aware 128x128 atlases for the native ecosystem.
# The geometry owns the silhouette; these atlases reinforce anatomy with broad
# Minecraft-style markings instead of the old all-over procedural noise.
function C([string]$Hex) {
    [System.Drawing.ColorTranslator]::FromHtml($Hex)
}

function Fill($Graphics, [string]$Hex, [int]$X, [int]$Y, [int]$W, [int]$H) {
    $brush = [System.Drawing.SolidBrush]::new((C $Hex))
    try {
        $Graphics.FillRectangle($brush, $X, $Y, $W, $H)
    }
    finally {
        $brush.Dispose()
    }
}

function Paint-AtlasBase($Graphics, [hashtable]$Palette) {
    Fill $Graphics $Palette.Base 0 0 128 128

    # Head and muzzle.
    Fill $Graphics $Palette.Light 2 2 24 5
    Fill $Graphics $Palette.Shadow 2 19 28 7
    Fill $Graphics $Palette.Muzzle 2 12 20 5

    # Body and shoulder island.
    Fill $Graphics $Palette.Shadow 0 32 62 7
    Fill $Graphics $Palette.Light 4 40 54 4
    Fill $Graphics $Palette.Base 4 46 54 15

    # Paired legs and feet.
    foreach ($x in 0, 16, 32, 48) {
        Fill $Graphics $Palette.Shadow $x 72 13 4
        Fill $Graphics $Palette.Base $x 76 13 12
        Fill $Graphics $Palette.Dark $x 88 13 6
    }

    # Tail, ears/horns, back ornament and wing islands.
    Fill $Graphics $Palette.Accent 64 0 56 18
    Fill $Graphics $Palette.Shadow 64 20 56 27
    Fill $Graphics $Palette.Base 64 48 56 22
    Fill $Graphics $Palette.Shadow 64 72 48 5
    Fill $Graphics $Palette.Base 64 77 48 18

    # Small high-contrast facial marks that survive down-sampling in game.
    Fill $Graphics $Palette.Dark 4 7 5 4
    Fill $Graphics $Palette.Dark 17 7 5 4
    Fill $Graphics $Palette.Eye 6 8 2 2
    Fill $Graphics $Palette.Eye 18 8 2 2
}

function Paint-Dapples($Graphics, [hashtable]$Palette, [int]$Offset) {
    foreach ($point in @(
        @(7, 47), @(17, 54), @(28, 43), @(39, 51), @(50, 45),
        @(69, 79), @(80, 86), @(91, 78), @(102, 88)
    )) {
        $x = ($point[0] + $Offset) % 112
        Fill $Graphics $Palette.Accent $x $point[1] 4 3
        Fill $Graphics $Palette.Light ($x + 1) $point[1] 2 1
    }
}

function Paint-Stripes($Graphics, [hashtable]$Palette, [int]$Width) {
    foreach ($x in 8, 20, 32, 44, 68, 82, 96, 110) {
        Fill $Graphics $Palette.Dark $x 35 $Width 25
        Fill $Graphics $Palette.Accent ($x + $Width) 35 2 25
    }
}

function Paint-Feathers($Graphics, [hashtable]$Palette) {
    foreach ($y in 49, 56, 63) {
        for ($x = 66; $x -lt 118; $x += 12) {
            Fill $Graphics $Palette.Shadow $x $y 9 5
            Fill $Graphics $Palette.Light ($x + 2) $y 5 2
            Fill $Graphics $Palette.Accent ($x + 4) ($y + 3) 5 2
        }
    }
}

function Paint-Species($Graphics, [string]$Id, [hashtable]$Palette) {
    switch ($Id) {
        'ash_hopper' {
            Paint-Dapples $Graphics $Palette 0
            Fill $Graphics $Palette.Light 0 80 30 6
            Fill $Graphics $Palette.Dark 0 88 30 6
            Fill $Graphics $Palette.Accent 64 0 12 18
        }
        'gravewing' {
            Paint-Feathers $Graphics $Palette
            foreach ($x in 68, 82, 96, 110) {
                Fill $Graphics $Palette.Accent $x 49 2 20
            }
            Fill $Graphics $Palette.Dark 0 32 62 10
            Fill $Graphics $Palette.Light 65 75 43 3
        }
        'rootback' {
            foreach ($x in 65, 79, 93, 107) {
                Fill $Graphics $Palette.Dark $x 21 11 25
                Fill $Graphics $Palette.Light ($x + 2) 23 7 4
            }
            Fill $Graphics $Palette.Accent 4 46 54 12
            Paint-Dapples $Graphics $Palette 7
        }
        'bark_marten' {
            Paint-Stripes $Graphics $Palette 4
            Fill $Graphics $Palette.Light 2 3 24 6
            Fill $Graphics $Palette.Dark 3 11 21 8
            Fill $Graphics $Palette.Muzzle 8 14 10 5
            Fill $Graphics $Palette.Light 64 88 45 5
        }
        'crag_ram' {
            Fill $Graphics $Palette.Light 0 32 62 22
            Paint-Dapples $Graphics $Palette 4
            foreach ($x in 65, 78, 91) {
                Fill $Graphics $Palette.Muzzle $x 1 9 16
                Fill $Graphics $Palette.Dark ($x + 2) 4 5 3
            }
            Fill $Graphics $Palette.Dark 0 88 62 7
        }
        'rift_puma' {
            Paint-Dapples $Graphics $Palette 11
            Fill $Graphics $Palette.Dark 0 32 62 5
            Fill $Graphics $Palette.Muzzle 2 13 22 5
            Fill $Graphics $Palette.Dark 64 78 43 4
            Fill $Graphics $Palette.Accent 69 84 5 5
            Fill $Graphics $Palette.Accent 88 84 5 5
        }
        'mire_toad' {
            Paint-Dapples $Graphics $Palette 3
            Fill $Graphics $Palette.Muzzle 0 20 31 8
            Fill $Graphics $Palette.Light 0 51 62 10
            Fill $Graphics $Palette.Accent 64 0 19 11
            Fill $Graphics $Palette.Eye 68 3 6 5
            Fill $Graphics $Palette.Eye 76 3 6 5
            Fill $Graphics $Palette.Dark 0 88 62 6
        }
        'reed_lynx' {
            Paint-Stripes $Graphics $Palette 3
            Fill $Graphics $Palette.Dark 64 0 25 18
            Fill $Graphics $Palette.Accent 72 0 4 18
            Fill $Graphics $Palette.Muzzle 3 13 20 5
            Fill $Graphics $Palette.Dark 0 88 62 6
        }
        'silt_ray' {
            Fill $Graphics $Palette.Dark 0 0 128 8
            foreach ($y in 18, 34, 50, 66, 82, 98) {
                Fill $Graphics $Palette.Shadow 8 $y 112 5
                Fill $Graphics $Palette.Light 18 ($y + 2) 92 2
            }
            foreach ($x in 18, 40, 62, 84, 106) {
                Fill $Graphics $Palette.Accent $x 13 3 88
            }
            Fill $Graphics $Palette.Eye 50 16 5 4
            Fill $Graphics $Palette.Eye 73 16 5 4
        }
        'ember_fox' {
            Fill $Graphics $Palette.Muzzle 2 12 23 8
            Fill $Graphics $Palette.Dark 64 0 24 18
            Fill $Graphics $Palette.Light 0 50 62 11
            Fill $Graphics $Palette.Dark 0 84 62 10
            Fill $Graphics $Palette.Light 93 78 16 17
        }
        'cinder_fowl' {
            Paint-Feathers $Graphics $Palette
            Fill $Graphics $Palette.Accent 64 0 20 18
            Fill $Graphics $Palette.Light 0 43 62 6
            Fill $Graphics $Palette.Dark 0 54 62 7
            Fill $Graphics $Palette.Muzzle 2 13 19 4
        }
        'pallid_hart' {
            Paint-Dapples $Graphics $Palette 5
            Fill $Graphics $Palette.Muzzle 1 13 23 7
            Fill $Graphics $Palette.Dark 64 0 56 18
            foreach ($x in 68, 80, 92, 104) {
                Fill $Graphics $Palette.Light $x 1 4 16
            }
            Fill $Graphics $Palette.Dark 0 89 62 5
        }
        'mossboar' {
            Fill $Graphics $Palette.Dark 0 32 62 14
            Fill $Graphics $Palette.Accent 0 46 62 14
            Fill $Graphics $Palette.Muzzle 0 13 29 12
            Fill $Graphics $Palette.Light 64 0 18 7
            Paint-Dapples $Graphics $Palette 9
        }
        'amber_jay' {
            Paint-Feathers $Graphics $Palette
            foreach ($x in 66, 78, 90, 102, 114) {
                Fill $Graphics $Palette.Accent $x 51 4 17
                Fill $Graphics $Palette.Light ($x + 1) 52 2 8
            }
            Fill $Graphics $Palette.Dark 0 12 25 6
            Fill $Graphics $Palette.Accent 64 78 45 6
        }
        'sunhorn' {
            Fill $Graphics $Palette.Light 0 45 62 11
            Fill $Graphics $Palette.Accent 0 56 62 5
            Fill $Graphics $Palette.Muzzle 2 13 20 6
            Fill $Graphics $Palette.Light 64 0 30 18
            Fill $Graphics $Palette.Dark 0 85 62 9
            Paint-Dapples $Graphics $Palette 2
        }
    }
}

function New-NativeAtlas([string]$Id, [hashtable]$Palette) {
    $bitmap = [System.Drawing.Bitmap]::new(
        128,
        128,
        [System.Drawing.Imaging.PixelFormat]::Format32bppArgb
    )
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
        Paint-AtlasBase $graphics $Palette
        Paint-Species $graphics $Id $Palette

        $path = Join-Path $script:ProjectRoot "src\main\resources\assets\gravesown\textures\entity\$Id.png"
        New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
        $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

$species = [ordered]@{
    ash_hopper  = @{ Base='#827964'; Shadow='#4f4b40'; Light='#b4aa8b'; Accent='#69715c'; Muzzle='#9b8e70'; Dark='#292a27'; Eye='#d8b95e' }
    gravewing   = @{ Base='#454d58'; Shadow='#272c34'; Light='#858e99'; Accent='#865745'; Muzzle='#616772'; Dark='#171a20'; Eye='#d9c87a' }
    rootback    = @{ Base='#4f5d46'; Shadow='#2c392c'; Light='#82936f'; Accent='#766044'; Muzzle='#657054'; Dark='#20261f'; Eye='#cdbd5e' }
    bark_marten = @{ Base='#765039'; Shadow='#3b2b24'; Light='#aa825f'; Accent='#c4a26d'; Muzzle='#d0b080'; Dark='#211a17'; Eye='#e3ca6d' }
    crag_ram    = @{ Base='#6d6b67'; Shadow='#3b3c3c'; Light='#aaa69f'; Accent='#887153'; Muzzle='#9b8668'; Dark='#252626'; Eye='#dabe64' }
    rift_puma   = @{ Base='#755b57'; Shadow='#3c3033'; Light='#a38178'; Accent='#554149'; Muzzle='#b29483'; Dark='#211c20'; Eye='#e4c36b' }
    mire_toad   = @{ Base='#5d7055'; Shadow='#33432f'; Light='#91a87c'; Accent='#886348'; Muzzle='#9f855f'; Dark='#20291f'; Eye='#d9c45d' }
    reed_lynx   = @{ Base='#817356'; Shadow='#474033'; Light='#b3a57b'; Accent='#59634d'; Muzzle='#c3b184'; Dark='#27231f'; Eye='#dabc55' }
    silt_ray    = @{ Base='#42646a'; Shadow='#243d43'; Light='#7b9ba0'; Accent='#667c62'; Muzzle='#6e8987'; Dark='#17272b'; Eye='#c7d57b' }
    ember_fox   = @{ Base='#9c5a34'; Shadow='#523022'; Light='#ce8752'; Accent='#d2a35e'; Muzzle='#e0bb79'; Dark='#2c1e18'; Eye='#f0d269' }
    cinder_fowl = @{ Base='#77473a'; Shadow='#3d2827'; Light='#ac715e'; Accent='#c88a42'; Muzzle='#d6a45d'; Dark='#241a1b'; Eye='#e9cf68' }
    pallid_hart = @{ Base='#a19b86'; Shadow='#5b5a50'; Light='#d0c8ac'; Accent='#778574'; Muzzle='#b8ae91'; Dark='#30312d'; Eye='#dabf63' }
    mossboar    = @{ Base='#58654a'; Shadow='#2e392b'; Light='#8b9875'; Accent='#70503a'; Muzzle='#9a7051'; Dark='#20251d'; Eye='#d9b95c' }
    amber_jay   = @{ Base='#b77c3a'; Shadow='#5d4429'; Light='#e0aa5a'; Accent='#4f777b'; Muzzle='#d59a49'; Dark='#2b2721'; Eye='#f1dd79' }
    sunhorn     = @{ Base='#ae8951'; Shadow='#5f4a30'; Light='#dfc17e'; Accent='#8a673d'; Muzzle='#c9a56c'; Dark='#30261c'; Eye='#f0e189' }
}

foreach ($entry in $species.GetEnumerator()) {
    New-NativeAtlas $entry.Key $entry.Value
}

Write-Host "PASS native fauna art: $($species.Count) distinct UV-aware 128x128 atlases" -ForegroundColor Green
