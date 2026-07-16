param(
    [switch]$WriteContactSheets
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

$assetRoot = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown'
$textureRoot = Join-Path $assetRoot 'textures'
$errors = [System.Collections.Generic.List[string]]::new()

function Read-Size([string]$Path) {
    $bitmap = [System.Drawing.Bitmap]::FromFile($Path)
    try { @($bitmap.Width, $bitmap.Height) }
    finally { $bitmap.Dispose() }
}

function Assert-Size([string]$Relative, [int]$Width, [int]$Height) {
    $path = Join-Path $assetRoot $Relative
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        $errors.Add("Missing texture: $Relative")
        return
    }
    $size = Read-Size $path
    if ($size[0] -ne $Width -or $size[1] -ne $Height) {
        $errors.Add("Wrong size for ${Relative}: $($size[0])x$($size[1]), expected ${Width}x${Height}")
    }
}

# Native gameplay scale is a contract, not a suggestion.  Higher fidelity comes
# from controlled clusters and geometry, not arbitrary 2x terrain pixels.
$specialBlocks = @{
    'gloamwater_still.png' = @(16, 96)
    'gloamwater_flow.png' = @(32, 192)
}
Get-ChildItem -LiteralPath (Join-Path $textureRoot 'block') -File -Filter '*.png' | ForEach-Object {
    $expected = if ($specialBlocks.ContainsKey($_.Name)) { $specialBlocks[$_.Name] } else { @(16, 16) }
    $size = Read-Size $_.FullName
    if ($size[0] -ne $expected[0] -or $size[1] -ne $expected[1]) {
        $errors.Add("Block texture $($_.Name) is $($size[0])x$($size[1]); expected $($expected[0])x$($expected[1])")
    }
}

Get-ChildItem -LiteralPath (Join-Path $textureRoot 'item') -File -Filter '*.png' | ForEach-Object {
    $size = Read-Size $_.FullName
    if (($size[0] -ne 16 -or $size[1] -ne 16) -and ($size[0] -ne 32 -or $size[1] -ne 32)) {
        $errors.Add("Item texture $($_.Name) is $($size[0])x$($size[1]); expected 16x16 or reviewed 32x32")
    }
}

Assert-Size 'textures\models\armor\quietskin_layer_1.png' 128 128
Assert-Size 'textures\models\armor\quietskin_layer_2.png' 128 128

$entitySizes = @{
    'hollow_grazer.png' = @(128,128); 'ribspring.png' = @(128,128)
    'stitchtusk.png' = @(128,128); 'woundscent.png' = @(128,128)
    'buried_remnant.png' = @(128,128); 'rotfin.png' = @(128,128)
    'veilfin.png' = @(128,128); 'rootskimmer.png' = @(128,128)
    'silt_ray.png' = @(128,128); 'ash_hopper.png' = @(128,128)
    'gravewing.png' = @(128,128); 'rootback.png' = @(128,128)
    'bark_marten.png' = @(128,128); 'crag_ram.png' = @(128,128)
    'rift_puma.png' = @(128,128); 'mire_toad.png' = @(128,128)
    'reed_lynx.png' = @(128,128); 'ember_fox.png' = @(128,128)
    'cinder_fowl.png' = @(128,128); 'pallid_hart.png' = @(128,128)
    'mossboar.png' = @(128,128); 'amber_jay.png' = @(128,128)
    'sunhorn.png' = @(128,128)
    'gloam_skiff.png' = @(128,64)
}
foreach ($entry in $entitySizes.GetEnumerator()) {
    Assert-Size ("textures\entity\" + $entry.Key) $entry.Value[0] $entry.Value[1]
}

# Every Gravesown texture id referenced by a model must resolve to a shipped PNG.
$modelRoot = Join-Path $assetRoot 'models'
Get-ChildItem -LiteralPath $modelRoot -Recurse -File -Filter '*.json' | ForEach-Object {
    $raw = Get-Content -LiteralPath $_.FullName -Raw -Encoding UTF8
    $model = $raw | ConvertFrom-Json
    $texturesProperty = $model.PSObject.Properties['textures']
    if ($null -eq $texturesProperty) { return }
    foreach ($property in $texturesProperty.Value.PSObject.Properties) {
        $value = [string]$property.Value
        $match = [regex]::Match($value, '^gravesown:(block|item)/([a-z0-9_./-]+)$')
        if (-not $match.Success) { continue }
        $kind = $match.Groups[1].Value
        $id = $match.Groups[2].Value
        $candidate = Join-Path $textureRoot "$kind\$id.png"
        if (-not (Test-Path -LiteralPath $candidate -PathType Leaf)) {
            $relativeModel = $_.FullName.Substring($script:ProjectRoot.Length).TrimStart('\','/')
            $errors.Add("Model $relativeModel references missing gravesown:$kind/$id")
        }
    }
}

function Write-ContactSheet([string[]]$RelativeTextures, [string]$Name, [int]$CellSize) {
    $columns = 6
    $rows = [int][Math]::Ceiling($RelativeTextures.Count / [double]$columns)
    $labelHeight = 22
    $sheetWidth = [int]($columns * $CellSize)
    $sheetHeight = [int]($rows * ($CellSize + $labelHeight))
    $sheet = [System.Drawing.Bitmap]::new($sheetWidth, $sheetHeight, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($sheet)
    # Match the shipped Cold Silence review environment so transparent pixels and
    # low-value materials are judged against the same navy backdrop as the UI.
    $background = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#071423'))
    $labelBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#D7E6F2'))
    $font = New-Object System.Drawing.Font('Consolas', 8)
    try {
        $graphics.FillRectangle($background, 0, 0, $sheet.Width, $sheet.Height)
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
        for ($index = 0; $index -lt $RelativeTextures.Count; $index++) {
            $column = $index % $columns
            $row = [int][Math]::Floor($index / $columns)
            $x = $column * $CellSize
            $y = $row * ($CellSize + $labelHeight)
            $path = Join-Path $textureRoot $RelativeTextures[$index]
            if (-not (Test-Path -LiteralPath $path)) { continue }
            $source = [System.Drawing.Bitmap]::FromFile($path)
            try {
                $graphics.DrawImage($source, [System.Drawing.Rectangle]::new($x, $y, $CellSize, $CellSize))
            }
            finally { $source.Dispose() }
            $label = [System.IO.Path]::GetFileNameWithoutExtension($RelativeTextures[$index])
            $graphics.DrawString($label, $font, $labelBrush, $x + 3, $y + $CellSize + 3)
        }

        $reportRoot = Join-Path $script:ProjectRoot 'build\reports\gravesown\art'
        New-Item -ItemType Directory -Force -Path $reportRoot | Out-Null
        $path = Join-Path $reportRoot $Name
        $sheet.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Host "Contact sheet: $path"
    }
    finally {
        $font.Dispose(); $labelBrush.Dispose(); $background.Dispose(); $graphics.Dispose(); $sheet.Dispose()
    }
}

if ($WriteContactSheets) {
    Write-ContactSheet @(
        'block\ashen_sod_top.png','block\ashen_sod_side.png','block\grave_loam.png',
        'block\rootfelt_top.png','block\rootfelt_side.png','block\fibrous_loam.png',
        'block\dried_ichor_top.png','block\dried_ichor_side.png','block\scar_shale.png',
        'block\marrowstone_top.png','block\marrowstone_side.png','block\suture_silt_top.png',
        'block\suture_silt_side.png','block\veined_shale.png',
        'block\splintered_marrowstone.png','block\cairnstone.png','block\gloam_sand.png',
        'block\gloam_muck.png','block\abyssal_silt.png','block\brinebone.png',
        'block\ribroot_planks.png','block\emberbark_planks.png','block\palevine_planks.png',
        'block\cairnwood_planks.png','block\suturewood_planks.png',
        'block\mosswake_planks.png','block\sunveil_planks.png',
        'block\rift_thorn.png','block\mire_frond.png','block\mossveil.png','block\amber_bloom.png'
    ) 'terrain-contact-sheet.png' 112
    Write-ContactSheet @(
        'entity\hollow_grazer.png','entity\ribspring.png','entity\stitchtusk.png',
        'entity\woundscent.png','entity\buried_remnant.png','entity\rotfin.png',
        'entity\veilfin.png','entity\rootskimmer.png','entity\silt_ray.png',
        'entity\ash_hopper.png','entity\gravewing.png','entity\rootback.png',
        'entity\bark_marten.png','entity\crag_ram.png','entity\rift_puma.png',
        'entity\mire_toad.png','entity\reed_lynx.png','entity\ember_fox.png',
        'entity\cinder_fowl.png','entity\pallid_hart.png','entity\mossboar.png',
        'entity\amber_jay.png','entity\sunhorn.png'
    ) 'entity-atlas-contact-sheet.png' 160

    $allBlocks = Get-ChildItem -LiteralPath (Join-Path $textureRoot 'block') -File -Filter '*.png' |
        Sort-Object Name |
        ForEach-Object { 'block\' + $_.Name }
    Write-ContactSheet $allBlocks 'block-catalog-contact-sheet.png' 96

    $allItems = Get-ChildItem -LiteralPath (Join-Path $textureRoot 'item') -File -Filter '*.png' |
        Sort-Object Name |
        ForEach-Object { 'item\' + $_.Name }
    Write-ContactSheet $allItems 'item-catalog-contact-sheet.png' 96
}

if ($errors.Count -gt 0) {
    $errors | ForEach-Object { Write-Host "FAIL $_" -ForegroundColor Red }
    throw "Art contract failed with $($errors.Count) problem(s)."
}

$textureCount = (Get-ChildItem -LiteralPath $textureRoot -Recurse -File -Filter '*.png').Count
Write-Host "PASS art contract: $textureCount Gravesown PNGs, native sizes and model texture references verified." -ForegroundColor Green
