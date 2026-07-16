Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

function Color([string]$hex) { [System.Drawing.ColorTranslator]::FromHtml($hex) }
function Brush([string]$hex) { New-Object System.Drawing.SolidBrush (Color $hex) }
function Fill($g, $b, [int]$x, [int]$y, [int]$w, [int]$h) { $g.FillRectangle($b, $x, $y, $w, $h) }
function Save($bitmap, [string]$relative) {
    $path = Join-Path $script:ProjectRoot $relative
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Created $relative"
}

$p = @{
    black = Brush '#111113'; pit = Brush '#1C1A20'; iron = Brush '#4B4A50'; ironLight = Brush '#77736E'
    wood = Brush '#39262B'; woodLight = Brush '#654048'; sinew = Brush '#A39370'; rust = Brush '#80383B'
    stone = Brush '#2A2930'; stoneLight = Brush '#55525E'; ember = Brush '#D15836'; glow = Brush '#E5A34B'
    flesh = Brush '#733B3E'; fleshLight = Brush '#A45A51'; kelp = Brush '#4B613E'
}

try {
    foreach ($face in @('bottom','top','front','side')) {
        $b = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g = [System.Drawing.Graphics]::FromImage($b)
        try {
            Fill $g $p.wood 0 0 16 16
            Fill $g $p.black 0 0 16 2
            Fill $g $p.black 0 14 16 2
            Fill $g $p.woodLight 2 2 12 2
            Fill $g $p.pit 2 5 12 7
            if ($face -eq 'top') {
                Fill $g $p.stone 3 3 10 10
                Fill $g $p.stoneLight 4 4 8 2
                Fill $g $p.black 7 3 2 10
                Fill $g $p.black 3 7 10 2
                foreach ($xy in @(@(5,5),@(10,5),@(5,10),@(10,10))) { Fill $g $p.sinew $xy[0] $xy[1] 1 1 }
            }
            elseif ($face -eq 'front') {
                Fill $g $p.iron 3 5 10 6
                Fill $g $p.black 5 6 6 4
                Fill $g $p.rust 6 7 4 2
                Fill $g $p.sinew 2 12 12 1
            }
            elseif ($face -eq 'side') {
                Fill $g $p.sinew 3 3 2 10
                Fill $g $p.sinew 11 3 2 10
                Fill $g $p.rust 5 7 6 2
            }
            else {
                Fill $g $p.iron 3 4 10 8
                Fill $g $p.black 5 6 6 4
            }
        } finally { $g.Dispose() }
        Save $b "src/main/resources/assets/gravesown/textures/block/gravework_bench_$face.png"
        $b.Dispose()
    }

    foreach ($face in @('top','bottom','side','front','front_on')) {
        $b = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g = [System.Drawing.Graphics]::FromImage($b)
        try {
            Fill $g $p.stone 0 0 16 16
            for ($y = 0; $y -lt 16; $y += 4) {
                Fill $g $p.stoneLight (($y * 3) % 11) $y 5 2
                Fill $g $p.black ((13 - $y) % 12) ($y + 2) 4 1
            }
            if ($face -eq 'front' -or $face -eq 'front_on') {
                Fill $g $p.iron 2 3 12 11
                Fill $g $p.black 4 6 8 6
                Fill $g $p.rust 3 4 10 2
                if ($face -eq 'front_on') {
                    Fill $g $p.ember 5 8 6 4
                    Fill $g $p.glow 7 8 2 3
                }
            }
            elseif ($face -eq 'top') {
                Fill $g $p.black 4 4 8 8
                Fill $g $p.rust 6 6 4 4
            }
            elseif ($face -eq 'bottom') {
                Fill $g $p.black 2 2 12 12
                Fill $g $p.stoneLight 4 4 8 8
            }
        } finally { $g.Dispose() }
        Save $b "src/main/resources/assets/gravesown/textures/block/pitch_kiln_$face.png"
        $b.Dispose()
    }

    foreach ($tool in @('hushstone_pickaxe','hushstone_shovel','hushstone_axe','hushstone_hoe')) {
        $b = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g = [System.Drawing.Graphics]::FromImage($b)
        try {
            $g.Clear([System.Drawing.Color]::Transparent)
            for ($i = 4; $i -le 13; $i++) { Fill $g $p.woodLight (14 - $i) $i 2 2 }
            Fill $g $p.sinew 6 7 4 2
            if ($tool -eq 'hushstone_pickaxe') {
                Fill $g $p.stone 2 2 11 3
                Fill $g $p.stoneLight 3 2 8 1
                Fill $g $p.rust 11 4 2 2
            }
            elseif ($tool -eq 'hushstone_shovel') {
                Fill $g $p.stone 2 2 5 6
                Fill $g $p.stoneLight 3 3 3 3
                Fill $g $p.rust 2 6 3 2
            }
            elseif ($tool -eq 'hushstone_axe') {
                Fill $g $p.stone 2 2 7 7
                Fill $g $p.stoneLight 3 3 4 3
                Fill $g $p.black 7 6 3 3
            }
            else {
                Fill $g $p.stone 2 3 8 3
                Fill $g $p.stoneLight 3 3 5 1
                Fill $g $p.rust 2 5 3 2
            }
        } finally { $g.Dispose() }
        Save $b "src/main/resources/assets/gravesown/textures/item/$tool.png"
        $b.Dispose()
    }

    foreach ($rod in @('gloamline_rod','gloamline_rod_cast')) {
        $b = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g = [System.Drawing.Graphics]::FromImage($b)
        try {
            $g.Clear([System.Drawing.Color]::Transparent)
            for ($i = 2; $i -le 13; $i++) { Fill $g $p.wood (14 - $i) $i 2 2 }
            Fill $g $p.sinew 2 3 6 1
            Fill $g $p.sinew 2 4 1 6
            Fill $g $p.rust 1 9 3 2
            if ($rod -eq 'gloamline_rod_cast') {
                Fill $g $p.sinew 1 2 1 9
                Fill $g $p.glow 0 11 2 2
            }
        } finally { $g.Dispose() }
        Save $b "src/main/resources/assets/gravesown/textures/item/$rod.png"
        $b.Dispose()
    }

    # Hushstone Spear: item and projectile art use natural timber/stone colors.
    $b = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($b)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        for ($i = 3; $i -le 14; $i++) { Fill $g $p.woodLight (15 - $i) $i 2 2 }
        Fill $g $p.sinew 7 8 4 2
        Fill $g $p.stone 9 1 5 7
        Fill $g $p.stoneLight 11 1 2 5
        Fill $g $p.black 9 6 3 2
    } finally { $g.Dispose() }
    Save $b 'src/main/resources/assets/gravesown/textures/item/hushstone_spear.png'
    $b.Dispose()

    $b = New-Object System.Drawing.Bitmap(32, 32, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($b)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        Fill $g $p.stone 0 0 8 8
        Fill $g $p.stoneLight 1 0 3 7
        Fill $g $p.black 6 1 2 7
        Fill $g $p.wood 0 8 4 24
        Fill $g $p.woodLight 1 8 1 24
        Fill $g $p.sinew 4 8 8 4
        Fill $g $p.rust 8 8 4 4
    } finally { $g.Dispose() }
    Save $b 'src/main/resources/assets/gravesown/textures/entity/hushstone_spear.png'
    $b.Dispose()

    foreach ($food in @('charred_grazer_meat','smoked_rotfin')) {
        $b = New-Object System.Drawing.Bitmap(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g = [System.Drawing.Graphics]::FromImage($b)
        try {
            $g.Clear([System.Drawing.Color]::Transparent)
            Fill $g $p.black 2 4 12 9
            Fill $g $p.flesh 3 5 10 7
            Fill $g $p.fleshLight 5 5 6 2
            Fill $g $p.rust 4 9 7 2
            if ($food -eq 'smoked_rotfin') { Fill $g $p.kelp 10 7 3 3 }
            else { Fill $g $p.sinew 11 8 3 2 }
        } finally { $g.Dispose() }
        Save $b "src/main/resources/assets/gravesown/textures/item/$food.png"
        $b.Dispose()
    }
}
finally {
    foreach ($brush in $p.Values) { $brush.Dispose() }
}
