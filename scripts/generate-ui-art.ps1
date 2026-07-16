[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

# Binding cold-navy UI contract. UI sprites use only this small ramp so
# vanilla screens, custom containers and the Codex remain visually coherent.
$UiInk = '#071423'
$UiPanel = '#10243B'
$UiSteel = '#294764'
$UiSelected = '#3F6385'
$UiMuted = '#8FAFC8'
$UiCyan = '#4CA8E8'
$UiText = '#D7E6F2'
$UiYellow = '#E6C84F'

function New-ArgbBitmap([int]$Width, [int]$Height) {
    return [System.Drawing.Bitmap]::new(
        $Width,
        $Height,
        [System.Drawing.Imaging.PixelFormat]::Format32bppArgb
    )
}

function Convert-Color([string]$Hex) {
    return [System.Drawing.ColorTranslator]::FromHtml($Hex)
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
    try {
        $Graphics.FillRectangle($brush, $X, $Y, $Width, $Height)
    }
    finally {
        $brush.Dispose()
    }
}

function Set-Pixel([System.Drawing.Bitmap]$Bitmap, [int]$X, [int]$Y, [string]$Hex) {
    $Bitmap.SetPixel($X, $Y, (Convert-Color $Hex))
}

function Save-Bitmap([System.Drawing.Bitmap]$Bitmap, [string]$RelativePath) {
    $output = Join-Path $script:ProjectRoot $RelativePath
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $output) | Out-Null
    $Bitmap.Save($output, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Generated $output"
}

function Save-WideWidget(
    [string]$Name,
    [string]$Outer,
    [string]$Inner,
    [string]$Edge,
    [string]$Glint
) {
    $bitmap = New-ArgbBitmap 200 20
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Fill-Rect $graphics $Outer 1 1 198 18
        Fill-Rect $graphics $Inner 2 2 196 16
        Fill-Rect $graphics $UiInk 3 3 194 14
        Fill-Rect $graphics $Inner 4 4 192 12

        # Continuous rails survive nine-slice scaling without interior brackets or dangling seams.
        foreach ($y in @(6, 10, 14)) {
            Fill-Rect $graphics $UiPanel 5 $y 190 1
        }
        Fill-Rect $graphics $Edge 3 2 194 1
        Fill-Rect $graphics $Edge 3 17 194 1
        Fill-Rect $graphics $Edge 2 3 1 14
        Fill-Rect $graphics $Edge 197 3 1 14
        Fill-Rect $graphics $Glint 3 2 194 1
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\widget\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-SliderHandle([string]$Name, [string]$Edge, [string]$Fill, [string]$Glint) {
    $bitmap = New-ArgbBitmap 8 20
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Fill-Rect $graphics $Edge 0 0 8 20
        Fill-Rect $graphics $UiInk 1 1 6 18
        Fill-Rect $graphics $Fill 2 2 4 16
        Fill-Rect $graphics $Glint 2 2 1 16
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\widget\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-Checkbox([string]$Name, [bool]$Selected, [bool]$Highlighted) {
    $bitmap = New-ArgbBitmap 20 20
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $edge = if ($Highlighted) { $UiCyan } else { $UiSteel }
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Fill-Rect $graphics $UiInk 1 1 18 18
        Fill-Rect $graphics $edge 2 2 16 1
        Fill-Rect $graphics $edge 2 17 16 1
        Fill-Rect $graphics $edge 2 2 1 16
        Fill-Rect $graphics $edge 17 2 1 16
        Fill-Rect $graphics $UiPanel 4 4 12 12
        if ($Selected) {
            foreach ($point in @(
                @(5, 10), @(6, 11), @(7, 12), @(8, 13),
                @(9, 12), @(10, 11), @(11, 10), @(12, 9), @(13, 8), @(14, 7)
            )) {
                Fill-Rect $graphics $UiText $point[0] $point[1] 2 2
            }
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\widget\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-MenuTile([string]$Name, [string]$Base, [string]$Patch, [string]$Accent) {
    $bitmap = New-ArgbBitmap 16 16
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        Fill-Rect $graphics $Base 0 0 16 16
        Fill-Rect $graphics $Patch 0 0 8 4
        Fill-Rect $graphics $Patch 10 7 6 4
        Fill-Rect $graphics $UiInk 1 12 9 2
        foreach ($point in @(@(3, 5), @(12, 2), @(7, 9), @(14, 14))) {
            Set-Pixel $bitmap $point[0] $point[1] $Accent
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Draw-PixelFrame(
    [System.Drawing.Graphics]$Graphics,
    [int]$X,
    [int]$Y,
    [int]$Width,
    [int]$Height,
    [string]$Edge,
    [string]$Fill
) {
    Fill-Rect $Graphics $UiInk $X $Y $Width $Height
    Fill-Rect $Graphics $Edge ($X + 1) ($Y + 1) ($Width - 2) ($Height - 2)
    Fill-Rect $Graphics $Fill ($X + 3) ($Y + 3) ($Width - 6) ($Height - 6)
}

function Draw-InventorySlot([System.Drawing.Graphics]$Graphics, [int]$X, [int]$Y) {
    Fill-Rect $Graphics $UiSteel $X $Y 18 18
    Fill-Rect $Graphics $UiInk ($X + 1) ($Y + 1) 16 16
    Fill-Rect $Graphics $UiPanel ($X + 2) ($Y + 2) 14 14
}

function Save-LockButton(
    [string]$Name,
    [bool]$Unlocked,
    [bool]$Highlighted,
    [bool]$Disabled
) {
    $bitmap = New-ArgbBitmap 20 20
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Disabled) { $UiSteel } elseif ($Highlighted) { $UiCyan } else { $UiSelected }
        $metal = if ($Disabled) { $UiSelected } else { $UiMuted }
        Draw-PixelFrame $graphics 1 1 18 18 $edge $UiPanel
        Fill-Rect $graphics $metal 6 8 9 8
        Fill-Rect $graphics $UiInk 8 11 2 3
        Fill-Rect $graphics $UiYellow 10 11 2 3
        if ($Unlocked) {
            Fill-Rect $graphics $metal 11 4 2 5
            Fill-Rect $graphics $metal 8 3 5 2
            Fill-Rect $graphics $UiPanel 8 5 3 3
        }
        else {
            Fill-Rect $graphics $metal 7 4 7 2
            Fill-Rect $graphics $metal 6 5 2 4
            Fill-Rect $graphics $metal 13 5 2 4
            Fill-Rect $graphics $UiPanel 8 6 5 2
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\widget\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-RecipeBookButton([string]$Name, [bool]$Highlighted) {
    $bitmap = New-ArgbBitmap 20 18
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Highlighted) { $UiCyan } else { $UiSteel }
        Fill-Rect $graphics $UiInk 2 1 16 16
        Fill-Rect $graphics $edge 3 2 14 14
        Fill-Rect $graphics $UiPanel 5 3 10 12
        Fill-Rect $graphics $UiSelected 5 3 2 12
        Fill-Rect $graphics $UiText 9 5 4 1
        Fill-Rect $graphics $UiMuted 8 8 5 1
        Fill-Rect $graphics $UiMuted 8 11 4 1
        Set-Pixel $bitmap 14 14 $edge
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\recipe_book\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-RecipeBookPanel() {
    $bitmap = New-ArgbBitmap 256 256
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Draw-PixelFrame $graphics 0 0 147 166 $UiCyan $UiPanel
        Fill-Rect $graphics $UiInk 7 31 133 124
        Fill-Rect $graphics $UiSteel 8 32 1 122
        Fill-Rect $graphics $UiSteel 138 32 1 122
        foreach ($y in @(39, 73, 107, 141)) {
            Fill-Rect $graphics $UiPanel 10 $y 126 1
        }
        Fill-Rect $graphics $UiCyan 5 4 20 2
        Fill-Rect $graphics $UiYellow 124 159 17 2
        Save-Bitmap $bitmap 'src\main\resources\assets\minecraft\textures\gui\recipe_book.png'
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-CreativeBackground([string]$Name, [bool]$InventoryLayout) {
    $bitmap = New-ArgbBitmap 256 256
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Draw-PixelFrame $graphics 0 0 195 136 $UiCyan $UiPanel
        # Vanilla creative labels use a dark font, so the compact title strip
        # remains pale while the rest of the inventory follows the navy ramp.
        Fill-Rect $graphics $UiMuted 3 3 189 13
        if ($InventoryLayout) {
            # Exact CreativeModeInventoryScreen 1.21.1 slot coordinates.
            # Armour is arranged in two columns around the player preview; the
            # offhand sits at 35,20; main inventory begins at y=54.
            # Each 18x18 well starts one pixel above and left of the actual Slot.
            # This is the same contract used by AbstractContainerScreen and keeps
            # the rendered item centred without a fake top-left offset.
            foreach ($point in @(@(53,5), @(53,32), @(107,5), @(107,32), @(34,19))) {
                Draw-InventorySlot $graphics $point[0] $point[1]
            }
            Fill-Rect $graphics $UiInk 72 5 36 47
            Fill-Rect $graphics $UiSteel 73 6 34 45
            for ($row = 0; $row -lt 3; $row++) {
                for ($column = 0; $column -lt 9; $column++) {
                    Draw-InventorySlot $graphics (8 + $column * 18) (53 + $row * 18)
                }
            }
            Draw-InventorySlot $graphics 172 111
        }
        else {
            for ($row = 0; $row -lt 5; $row++) {
                for ($column = 0; $column -lt 9; $column++) {
                    Draw-InventorySlot $graphics (8 + $column * 18) (17 + $row * 18)
                }
            }
            Fill-Rect $graphics $UiSteel 174 18 14 112
            Fill-Rect $graphics $UiInk 176 20 10 108
        }
        $hotbarStart = 8
        for ($column = 0; $column -lt 9; $column++) {
            Draw-InventorySlot $graphics ($hotbarStart + $column * 18) 111
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\container\creative_inventory\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-CreativeScroller([string]$Name, [bool]$Disabled) {
    $bitmap = New-ArgbBitmap 12 15
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Disabled) { $UiSteel } else { $UiCyan }
        Draw-PixelFrame $graphics 0 0 12 15 $edge $UiPanel
        Fill-Rect $graphics $edge 4 4 4 1
        Fill-Rect $graphics $edge 4 7 4 1
        Fill-Rect $graphics $edge 4 10 4 1
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\container\creative_inventory\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-CreativeTab([string]$Name, [bool]$Selected, [bool]$Top) {
    $bitmap = New-ArgbBitmap 26 32
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Selected) { $UiCyan } else { $UiSteel }
        $fill = if ($Selected) { $UiSteel } else { $UiPanel }
        $y = if ($Top) { 0 } else { 3 }
        $height = 29
        Draw-PixelFrame $graphics 0 $y 26 $height $edge $fill
        if ($Selected -and $Top) {
            # Merge cleanly into the matching creative background without the
            # one-pixel underline previously visible below the selected icon.
            Fill-Rect $graphics $fill 2 27 22 5
        }
        if ($Selected -and -not $Top) { Fill-Rect $graphics $fill 3 0 20 6 }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\container\creative_inventory\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-RecipeTab([string]$Name, [bool]$Selected) {
    $bitmap = New-ArgbBitmap 35 27
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Selected) { $UiCyan } else { $UiSteel }
        $fill = if ($Selected) { $UiSteel } else { $UiPanel }
        Draw-PixelFrame $graphics 0 0 35 27 $edge $fill
        if ($Selected) { Fill-Rect $graphics $UiSteel 31 3 4 21 }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\recipe_book\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-CelestialTextures {
    $sun = New-ArgbBitmap 32 32
    try {
        for ($y = 0; $y -lt 32; $y++) {
            for ($x = 0; $x -lt 32; $x++) {
                $dx = $x - 15.5
                $dy = $y - 15.5
                $distance = [Math]::Sqrt($dx * $dx + $dy * $dy)
                if ($distance -le 13.2 -and $distance -gt 11.4) {
                    $sun.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(158, 76, 168, 232))
                }
                elseif ($distance -le 11.4) {
                    $tone = if ((($x * 3 + $y * 5) % 7) -lt 2) { $UiMuted } else { $UiText }
                    $sun.SetPixel($x, $y, (Convert-Color $tone))
                }
            }
        }
        foreach ($point in @(@(10,8),@(11,9),@(12,10),@(13,11),@(13,12),@(18,7),@(18,8),@(17,9),@(16,10),@(20,18),@(19,19),@(18,20),@(8,18),@(9,18),@(10,17))) {
            $sun.SetPixel($point[0], $point[1], (Convert-Color $UiSelected))
        }
        Save-Bitmap $sun 'src\main\resources\assets\minecraft\textures\environment\sun.png'
    }
    finally { $sun.Dispose() }

    $moon = New-ArgbBitmap 64 32
    try {
        $shadowLimits = @(-1, 3, 7, 11, 16, 11, 7, 3)
        for ($phase = 0; $phase -lt 8; $phase++) {
            $tileX = ($phase % 4) * 16
            $tileY = [Math]::Floor($phase / 4) * 16
            for ($y = 1; $y -lt 15; $y++) {
                for ($x = 1; $x -lt 15; $x++) {
                    $dx = $x - 7.5
                    $dy = $y - 7.5
                    if (($dx * $dx + $dy * $dy) -gt 42.0) { continue }
                    $shadowed = if ($phase -eq 4) { $true }
                        elseif ($phase -lt 4) { $x -lt $shadowLimits[$phase] }
                        else { $x -gt (15 - $shadowLimits[$phase]) }
                    $color = if ($shadowed) { $UiPanel }
                        elseif ((($x + $y * 3 + $phase) % 8) -eq 0) { $UiSelected }
                        else { $UiMuted }
                    $moon.SetPixel($tileX + $x, $tileY + $y, (Convert-Color $color))
                }
            }
            $moon.SetPixel($tileX + 5, $tileY + 5, (Convert-Color $UiSteel))
            $moon.SetPixel($tileX + 10, $tileY + 9, (Convert-Color $UiSteel))
        }
        Save-Bitmap $moon 'src\main\resources\assets\minecraft\textures\environment\moon_phases.png'
    }
    finally { $moon.Dispose() }
}

function Save-RecipeSlot([string]$Name, [bool]$Craftable, [bool]$Many) {
    $bitmap = New-ArgbBitmap 25 25
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Craftable) { $UiCyan } else { $UiSelected }
        Draw-PixelFrame $graphics 0 0 25 25 $edge $UiPanel
        if ($Many) {
            Fill-Rect $graphics $edge 19 2 4 4
            Fill-Rect $graphics $UiInk 20 3 2 2
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\recipe_book\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-RecipePageArrow([string]$Name, [bool]$Forward, [bool]$Highlighted) {
    $bitmap = New-ArgbBitmap 12 17
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Highlighted) { $UiCyan } else { $UiSelected }
        Draw-PixelFrame $graphics 0 0 12 17 $edge $UiPanel
        for ($step = 0; $step -lt 4; $step++) {
            $x = if ($Forward) { 4 + $step } else { 7 - $step }
            Set-Pixel $bitmap $x (5 + $step) $edge
            Set-Pixel $bitmap $x (11 - $step) $edge
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\recipe_book\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-RecipeFilter([string]$Name, [bool]$Enabled, [bool]$Highlighted) {
    $bitmap = New-ArgbBitmap 26 16
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Highlighted) { $UiCyan } else { $UiSelected }
        Draw-PixelFrame $graphics 0 0 26 16 $edge $UiPanel
        if ($Enabled) {
            Fill-Rect $graphics $UiSelected 7 8 3 2
            Fill-Rect $graphics $UiCyan 9 10 3 2
            Fill-Rect $graphics $UiCyan 11 6 2 5
            Fill-Rect $graphics $UiCyan 13 4 4 2
        }
        else {
            for ($step = 0; $step -lt 7; $step++) {
                Set-Pixel $bitmap (9 + $step) (4 + $step) $UiMuted
                Set-Pixel $bitmap (15 - $step) (4 + $step) $UiMuted
            }
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\recipe_book\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-RecipeOverlay([string]$Name, [bool]$Highlighted, [bool]$Disabled) {
    $bitmap = New-ArgbBitmap 24 24
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Disabled) { $UiSteel } elseif ($Highlighted) { $UiCyan } else { $UiSelected }
        Draw-PixelFrame $graphics 0 0 24 24 $edge $UiPanel
        Fill-Rect $graphics $UiSteel 5 5 14 14
        Fill-Rect $graphics $edge 7 7 10 1
        Fill-Rect $graphics $edge 7 11 10 1
        Fill-Rect $graphics $edge 7 15 10 1
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\recipe_book\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-HudHeart(
    [string]$Name,
    [string]$Fill,
    [bool]$Half,
    [bool]$Blinking
) {
    $bitmap = New-ArgbBitmap 9 9
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Blinking) { $UiText } else { $UiInk }
        foreach ($span in @(@(1,0,3), @(5,0,3), @(0,1,9), @(0,2,9), @(1,3,7), @(1,4,7), @(2,5,5), @(2,6,5), @(3,7,3), @(4,8,1))) {
            Fill-Rect $graphics $edge $span[0] $span[1] $span[2] 1
        }
        if ($Fill -ne '') {
            $widths = if ($Half) { @(2,4,4,3,3,2,2,1) } else { @(2,7,7,5,5,3,3,1) }
            $starts = if ($Half) { @(1,1,1,2,2,3,3,4) } else { @(1,1,1,2,2,3,3,4) }
            for ($row = 1; $row -le 8; $row++) {
                Fill-Rect $graphics $Fill $starts[$row - 1] $row $widths[$row - 1] 1
            }
            Set-Pixel $bitmap 2 1 $UiText
            if (-not $Half) { Set-Pixel $bitmap 6 2 $UiSteel }
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\hud\heart\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-HudFood([string]$Name, [int]$Amount, [bool]$Hunger) {
    $bitmap = New-ArgbBitmap 9 9
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $edge = if ($Hunger) { $UiSteel } else { $UiInk }
        Fill-Rect $graphics $edge 2 0 4 1
        Fill-Rect $graphics $edge 1 1 6 2
        Fill-Rect $graphics $edge 0 3 8 3
        Fill-Rect $graphics $edge 1 6 6 2
        Fill-Rect $graphics $edge 3 8 3 1
        Fill-Rect $graphics $UiInk 2 2 4 4
        if ($Amount -gt 0) {
            $fill = if ($Hunger) { $UiSelected } else { $UiMuted }
            $left = if ($Amount -eq 1) { 4 } else { 2 }
            $width = if ($Amount -eq 1) { 2 } else { 4 }
            Fill-Rect $graphics $fill $left 2 $width 4
            Fill-Rect $graphics $UiText ([Math]::Max(3, $left)) 2 1 2
            Fill-Rect $graphics $UiSteel 2 5 4 1
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\hud\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-HudExperience([string]$Name, [bool]$Progress) {
    $bitmap = New-ArgbBitmap 182 5
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        Fill-Rect $graphics $UiInk 0 0 182 5
        Fill-Rect $graphics $UiSteel 1 1 180 3
        $fill = if ($Progress) { $UiCyan } else { $UiPanel }
        Fill-Rect $graphics $fill 2 2 178 1
        if ($Progress) {
            for ($x = 4; $x -lt 178; $x += 9) { Set-Pixel $bitmap $x 2 $UiText }
        }
        Save-Bitmap $bitmap "src\main\resources\assets\minecraft\textures\gui\sprites\hud\$Name.png"
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

Save-WideWidget 'button' $UiSteel $UiPanel $UiSelected $UiMuted
Save-WideWidget 'button_highlighted' $UiCyan $UiSteel $UiCyan $UiText
Save-WideWidget 'button_disabled' $UiPanel $UiInk $UiSteel $UiSelected
Save-WideWidget 'slider' $UiSteel $UiPanel $UiSelected $UiMuted
Save-WideWidget 'slider_highlighted' $UiCyan $UiSteel $UiCyan $UiText
Save-WideWidget 'text_field' $UiSteel $UiInk $UiSelected $UiMuted
Save-WideWidget 'text_field_highlighted' $UiCyan $UiPanel $UiCyan $UiText
Save-SliderHandle 'slider_handle' $UiSelected $UiMuted $UiText
Save-SliderHandle 'slider_handle_highlighted' $UiCyan $UiText $UiMuted
Save-Checkbox 'checkbox' $false $false
Save-Checkbox 'checkbox_highlighted' $false $true
Save-Checkbox 'checkbox_selected' $true $false
Save-Checkbox 'checkbox_selected_highlighted' $true $true
Save-MenuTile 'menu_background' $UiInk $UiPanel $UiCyan
Save-MenuTile 'menu_list_background' $UiInk $UiPanel $UiSteel
Save-MenuTile 'inworld_menu_background' $UiPanel $UiSteel $UiCyan
Save-MenuTile 'inworld_menu_list_background' $UiInk $UiPanel $UiSelected

Save-LockButton 'locked_button' $false $false $false
Save-LockButton 'locked_button_highlighted' $false $true $false
Save-LockButton 'locked_button_disabled' $false $false $true
Save-LockButton 'unlocked_button' $true $false $false
Save-LockButton 'unlocked_button_highlighted' $true $true $false
Save-LockButton 'unlocked_button_disabled' $true $false $true

Save-RecipeBookButton 'button' $false
Save-RecipeBookButton 'button_highlighted' $true
Save-RecipeBookPanel
Save-RecipeTab 'tab' $false
Save-RecipeTab 'tab_selected' $true
Save-RecipeSlot 'slot_craftable' $true $false
Save-RecipeSlot 'slot_uncraftable' $false $false
Save-RecipeSlot 'slot_many_craftable' $true $true
Save-RecipeSlot 'slot_many_uncraftable' $false $true
Save-RecipePageArrow 'page_forward' $true $false
Save-RecipePageArrow 'page_forward_highlighted' $true $true
Save-RecipePageArrow 'page_backward' $false $false
Save-RecipePageArrow 'page_backward_highlighted' $false $true
Save-RecipeFilter 'filter_enabled' $true $false
Save-RecipeFilter 'filter_enabled_highlighted' $true $true
Save-RecipeFilter 'filter_disabled' $false $false
Save-RecipeFilter 'filter_disabled_highlighted' $false $true
Save-RecipeFilter 'furnace_filter_enabled' $true $false
Save-RecipeFilter 'furnace_filter_enabled_highlighted' $true $true
Save-RecipeFilter 'furnace_filter_disabled' $false $false
Save-RecipeFilter 'furnace_filter_disabled_highlighted' $false $true
Save-RecipeOverlay 'crafting_overlay' $false $false
Save-RecipeOverlay 'crafting_overlay_highlighted' $true $false
Save-RecipeOverlay 'crafting_overlay_disabled' $false $true
Save-RecipeOverlay 'crafting_overlay_disabled_highlighted' $true $true
Save-RecipeOverlay 'furnace_overlay' $false $false
Save-RecipeOverlay 'furnace_overlay_highlighted' $true $false
Save-RecipeOverlay 'furnace_overlay_disabled' $false $true
Save-RecipeOverlay 'furnace_overlay_disabled_highlighted' $true $true

Save-CreativeBackground 'tab_items' $false
Save-CreativeBackground 'tab_item_search' $false
Save-CreativeBackground 'tab_inventory' $true
Save-CreativeScroller 'scroller' $false
Save-CreativeScroller 'scroller_disabled' $true
foreach ($index in 1..7) {
    Save-CreativeTab "tab_top_selected_$index" $true $true
    Save-CreativeTab "tab_top_unselected_$index" $false $true
    Save-CreativeTab "tab_bottom_selected_$index" $true $false
    Save-CreativeTab "tab_bottom_unselected_$index" $false $false
}
Save-HudHeart 'container' '' $false $false
Save-HudHeart 'container_blinking' '' $false $true
Save-HudHeart 'container_hardcore' '' $false $false
Save-HudHeart 'container_hardcore_blinking' '' $false $true
foreach ($kind in @(
    @('full', $UiMuted), @('hardcore_full', $UiMuted),
    @('poisoned_full', $UiSelected), @('poisoned_hardcore_full', $UiSelected),
    @('withered_full', $UiSteel), @('withered_hardcore_full', $UiSteel),
    @('absorbing_full', $UiYellow), @('absorbing_hardcore_full', $UiYellow),
    @('frozen_full', $UiCyan), @('frozen_hardcore_full', $UiCyan)
)) {
    Save-HudHeart $kind[0] $kind[1] $false $false
    Save-HudHeart ($kind[0] + '_blinking') $kind[1] $false $true
    $halfName = $kind[0] -replace 'full$', 'half'
    Save-HudHeart $halfName $kind[1] $true $false
    Save-HudHeart ($halfName + '_blinking') $kind[1] $true $true
}
Save-HudFood 'food_empty' 0 $false
Save-HudFood 'food_half' 1 $false
Save-HudFood 'food_full' 2 $false
Save-HudFood 'food_empty_hunger' 0 $true
Save-HudFood 'food_half_hunger' 1 $true
Save-HudFood 'food_full_hunger' 2 $true
Save-HudExperience 'experience_bar_background' $false
Save-HudExperience 'experience_bar_progress' $true
Save-CelestialTextures
