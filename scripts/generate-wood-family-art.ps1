Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

$utf8 = New-Object System.Text.UTF8Encoding($false)

function Write-GeneratedText([string]$relativePath, [string]$content) {
    $path = Join-Path $script:ProjectRoot $relativePath
    $directory = Split-Path -Parent $path
    [System.IO.Directory]::CreateDirectory($directory) | Out-Null
    [System.IO.File]::WriteAllText($path, $content, $utf8)
}

function Copy-TemplateFamily([string]$prefix) {
    $pairs = @(
        @('ribroot_stem', "$prefix`_stem"),
        @('ribroot_planks', "$prefix`_planks"),
        @('ribroot_stairs', "$prefix`_stairs"),
        @('ribroot_slab', "$prefix`_slab"),
        @('ribroot_fence', "$prefix`_fence"),
        @('ribroot_fence_gate', "$prefix`_fence_gate"),
        @('ribroot_door', "$prefix`_door"),
        @('ribroot_trapdoor', "$prefix`_trapdoor"),
        @('veil_foliage', "$prefix`_foliage"),
        @('ribroot_shoot', "$prefix`_shoot")
    )
    $roots = @(
        'src\main\resources\assets\gravesown\blockstates',
        'src\main\resources\assets\gravesown\models\block',
        'src\main\resources\assets\gravesown\models\item',
        'src\main\resources\data\gravesown\loot_table\blocks'
    )
    foreach ($pair in $pairs) {
        foreach ($root in $roots) {
            $sourcePattern = Join-Path $root ($pair[0] + '*.json')
            foreach ($source in Get-ChildItem -Path $sourcePattern -File -ErrorAction SilentlyContinue) {
                $targetName = $source.Name.Replace($pair[0], $pair[1])
                $target = Join-Path $root $targetName
                $content = [System.IO.File]::ReadAllText($source.FullName)
                # A copied shape can reference another member of the source family
                # (stairs/fences/slabs all sample Ribroot Planks). Convert every
                # family token, not only the id used in the current file name.
                foreach ($mapping in $pairs) {
                    $content = $content.Replace($mapping[0], $mapping[1])
                }
                Write-GeneratedText $target $content
            }
        }
    }

    foreach ($recipe in @('door','fence','fence_gate','planks_from_stem','slab','stairs','trapdoor')) {
        $source = Join-Path $script:ProjectRoot "src\main\resources\data\gravesown\recipe\ribroot_$recipe.json"
        $target = "src\main\resources\data\gravesown\recipe\$prefix`_$recipe.json"
        $content = [System.IO.File]::ReadAllText($source).Replace('ribroot', $prefix)
        # Splints are a shared structural component rather than a colour-matched
        # decoration. New timber families deliberately reuse the existing
        # Ribroot Splint so generators never invent unregistered items.
        $content = $content.Replace("$prefix`_splint", 'ribroot_splint')
        Write-GeneratedText $target $content
    }
}

function Copy-CutPlankFamily([string]$prefix) {
    $sourceId = 'ribroot_cut_planks'
    $targetId = "$prefix`_cut_planks"
    $roots = @(
        'src\main\resources\assets\gravesown\blockstates',
        'src\main\resources\assets\gravesown\models\block',
        'src\main\resources\assets\gravesown\models\item',
        'src\main\resources\data\gravesown\loot_table\blocks'
    )
    foreach ($root in $roots) {
        $source = Join-Path $script:ProjectRoot "$root\$sourceId.json"
        if (-not (Test-Path -LiteralPath $source)) { continue }
        $content = [System.IO.File]::ReadAllText($source).Replace($sourceId, $targetId)
        Write-GeneratedText "$root\$targetId.json" $content
    }

    $sourceRecipe = Join-Path $script:ProjectRoot 'src\main\resources\data\gravesown\recipe\ribroot_cut_planks_from_sawmill.json'
    if (Test-Path -LiteralPath $sourceRecipe) {
        $content = [System.IO.File]::ReadAllText($sourceRecipe).Replace('ribroot', $prefix)
        Write-GeneratedText "src\main\resources\data\gravesown\recipe\$prefix`_cut_planks_from_sawmill.json" $content
    }
}

function New-Bitmap([int]$width = 16, [int]$height = 16) {
    New-Object System.Drawing.Bitmap $width, $height, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function Color([string]$hex) { [System.Drawing.ColorTranslator]::FromHtml($hex) }
function Brush([string]$hex) { New-Object System.Drawing.SolidBrush (Color $hex) }

function Save-Png($bitmap, [string]$relativePath) {
    $path = Join-Path $script:ProjectRoot $relativePath
    [System.IO.Directory]::CreateDirectory((Split-Path -Parent $path)) | Out-Null
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
}

function Save-WoodTile([string]$name, [hashtable]$palette) {
    $bitmap = New-Bitmap
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    try {
        $graphics.FillRectangle($base, 0, 0, 16, 16)
        # One shared Minecraft-like grain grammar for every public species:
        # broad clustered fibres, two restrained knots and no repeated full-width
        # courses that can read as brickwork when the tile repeats.
        foreach ($cluster in @(
            @(0, 1, 5, 2), @(8, 0, 6, 2), @(3, 5, 7, 2),
            @(11, 6, 5, 2), @(0, 10, 6, 2), @(8, 12, 7, 2)
        )) {
            $graphics.FillRectangle($mid, $cluster[0], $cluster[1], $cluster[2], $cluster[3])
        }
        foreach ($grain in @(
            @(1, 3, 4, 1), @(6, 2, 3, 1), @(10, 4, 5, 1),
            @(1, 8, 6, 1), @(9, 9, 4, 1), @(3, 14, 5, 1), @(11, 15, 4, 1)
        )) {
            $graphics.FillRectangle($dark, $grain[0], $grain[1], $grain[2], $grain[3])
        }
        foreach ($highlight in @(
            @(1, 1, 3, 1), @(9, 1, 4, 1), @(4, 5, 4, 1),
            @(1, 10, 3, 1), @(9, 12, 4, 1)
        )) {
            $graphics.FillRectangle($light, $highlight[0], $highlight[1], $highlight[2], $highlight[3])
        }
        $graphics.FillRectangle($dark, 12, 3, 2, 2)
        $graphics.FillRectangle($mid, 12, 3, 1, 1)
        $graphics.FillRectangle($dark, 5, 11, 2, 2)
        $graphics.FillRectangle($light, 5, 11, 1, 1)
        Save-Png $bitmap "src\main\resources\assets\gravesown\textures\block\$name.png"
    }
    finally { foreach ($item in @($graphics,$base,$mid,$dark,$light)) { $item.Dispose() }; $bitmap.Dispose() }
}

function Save-CutWoodTile([string]$name, [hashtable]$palette) {
    $bitmap = New-Bitmap
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    try {
        $graphics.FillRectangle($base, 0, 0, 16, 16)
        # Sawn boards are more regular than bark/planks, but staggered ends and
        # short fibres prevent the old full-width brick-wall effect.
        foreach ($y in @(0,5,10,15)) { $graphics.FillRectangle($dark, 0, $y, 16, 1) }
        foreach ($board in @(
            @(1,2,5,1), @(8,3,6,1), @(2,7,7,1),
            @(11,8,4,1), @(1,12,4,1), @(7,13,7,1)
        )) {
            $graphics.FillRectangle($mid, $board[0], $board[1], $board[2], $board[3])
        }
        foreach ($highlight in @(@(2,1,4,1),@(9,6,5,1),@(3,11,5,1))) {
            $graphics.FillRectangle($light, $highlight[0], $highlight[1], $highlight[2], $highlight[3])
        }
        $graphics.FillRectangle($dark, 6, 1, 1, 4)
        $graphics.FillRectangle($dark, 10, 6, 1, 4)
        $graphics.FillRectangle($dark, 5, 11, 1, 4)
        Save-Png $bitmap "src\main\resources\assets\gravesown\textures\block\$name.png"
    }
    finally { foreach ($item in @($graphics,$base,$mid,$dark,$light)) { $item.Dispose() }; $bitmap.Dispose() }
}

function Save-Stem([string]$prefix, [hashtable]$palette) {
    $bitmap = New-Bitmap; $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    try {
        $graphics.FillRectangle($base,0,0,16,16)
        # Broken vertical clusters keep bark direction readable without producing
        # evenly spaced candy stripes around a trunk.
        foreach ($cluster in @(
            @(0,0,3,5), @(1,8,2,8), @(5,3,3,7), @(6,13,2,3),
            @(10,0,3,6), @(11,9,3,6), @(15,2,1,7)
        )) {
            $graphics.FillRectangle($mid,$cluster[0],$cluster[1],$cluster[2],$cluster[3])
        }
        foreach ($cleft in @(
            @(3,1,1,6), @(4,10,1,5), @(8,0,1,4), @(9,7,1,7), @(14,11,1,5)
        )) {
            $graphics.FillRectangle($dark,$cleft[0],$cleft[1],$cleft[2],$cleft[3])
        }
        $graphics.FillRectangle($light,1,1,1,3)
        $graphics.FillRectangle($light,6,4,1,4)
        $graphics.FillRectangle($light,11,1,1,3)
        $graphics.FillRectangle($light,12,10,1,3)
        Save-Png $bitmap "src\main\resources\assets\gravesown\textures\block\$prefix`_stem.png"
    }
    finally { foreach ($item in @($graphics,$base,$mid,$dark,$light)) { $item.Dispose() }; $bitmap.Dispose() }

    $top = New-Bitmap; $g = [System.Drawing.Graphics]::FromImage($top)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    try {
        $g.FillRectangle($dark,0,0,16,16)
        $g.FillRectangle($base,1,1,14,14)
        $g.FillRectangle($mid,3,2,10,12)
        $g.FillRectangle($base,4,4,8,8)
        $g.FillRectangle($dark,6,5,5,6)
        $g.FillRectangle($mid,7,6,3,4)
        $g.FillRectangle($light,7,6,2,2)
        $g.FillRectangle($dark,1,7,3,2)
        $g.FillRectangle($light,11,3,2,1)
        Save-Png $top "src\main\resources\assets\gravesown\textures\block\$prefix`_stem_top.png"
    }
    finally { foreach ($item in @($g,$base,$mid,$dark,$light)) { $item.Dispose() }; $top.Dispose() }
}

function Save-Cutout([string]$name, [hashtable]$palette, [bool]$foliage) {
    $bitmap = New-Bitmap; $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([System.Drawing.Color]::Transparent)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    try {
        if ($foliage) {
            foreach ($box in @(@(0,2,6,5),@(5,0,6,7),@(10,3,6,6),@(2,8,7,6),@(9,9,6,5))) {
                $graphics.FillRectangle($base,$box[0],$box[1],$box[2],$box[3])
            }
            $graphics.FillRectangle($mid,3,3,4,3); $graphics.FillRectangle($mid,9,6,4,4)
            $graphics.FillRectangle($dark,1,11,4,2); $graphics.FillRectangle($light,7,2,3,2)
        }
        else {
            $graphics.FillRectangle($dark,7,5,2,11)
            $graphics.FillRectangle($base,3,5,10,2); $graphics.FillRectangle($base,5,2,6,3)
            $graphics.FillRectangle($mid,1,7,6,3); $graphics.FillRectangle($mid,9,8,6,3)
            $graphics.FillRectangle($light,7,1,2,3)
        }
        Save-Png $bitmap "src\main\resources\assets\gravesown\textures\block\$name.png"
    }
    finally { foreach ($item in @($graphics,$base,$mid,$dark,$light)) { $item.Dispose() }; $bitmap.Dispose() }
}

function Save-DoorArt([string]$prefix, [hashtable]$palette) {
    $bottom = New-Bitmap; $g = [System.Drawing.Graphics]::FromImage($bottom)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        $g.FillRectangle($dark, 0, 0, 16, 16)
        $g.FillRectangle($base, 1, 0, 14, 16)
        $g.FillRectangle($dark, 2, 0, 2, 16); $g.FillRectangle($dark, 12, 0, 2, 16)
        $g.FillRectangle($mid, 4, 2, 8, 2); $g.FillRectangle($mid, 4, 12, 8, 2)
        $g.FillRectangle($dark, 5, 5, 6, 6); $g.FillRectangle($base, 6, 6, 4, 4)
        $g.FillRectangle($light, 6, 6, 4, 1); $g.FillRectangle($mid, 6, 9, 4, 1)
        $g.FillRectangle($light, 10, 8, 1, 2)
        Save-Png $bottom "src\main\resources\assets\gravesown\textures\block\$prefix`_door_bottom.png"
    }
    finally { foreach ($item in @($g,$base,$mid,$dark,$light)) { $item.Dispose() }; $bottom.Dispose() }

    $top = New-Bitmap; $g = [System.Drawing.Graphics]::FromImage($top)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    $clear = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::Transparent)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        $g.FillRectangle($dark, 0, 0, 16, 16)
        $g.FillRectangle($base, 1, 0, 14, 16)
        $g.FillRectangle($dark, 2, 0, 2, 16); $g.FillRectangle($dark, 12, 0, 2, 16)
        $g.FillRectangle($mid, 4, 2, 8, 2); $g.FillRectangle($mid, 4, 13, 8, 2)
        $g.FillRectangle($dark, 4, 4, 8, 8)
        $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
        $g.FillRectangle($clear, 5, 5, 6, 6)
        $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceOver
        $g.FillRectangle($mid, 7, 5, 2, 6); $g.FillRectangle($mid, 5, 7, 6, 2)
        $g.FillRectangle($light, 7, 5, 1, 6); $g.FillRectangle($light, 5, 7, 6, 1)
        Save-Png $top "src\main\resources\assets\gravesown\textures\block\$prefix`_door_top.png"
    }
    finally { foreach ($item in @($g,$base,$mid,$dark,$light,$clear)) { $item.Dispose() }; $top.Dispose() }

    $trapdoor = New-Bitmap; $g = [System.Drawing.Graphics]::FromImage($trapdoor)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        $g.FillRectangle($dark, 0, 0, 16, 16); $g.FillRectangle($base, 2, 2, 12, 12)
        $g.FillRectangle($mid, 3, 3, 10, 2); $g.FillRectangle($mid, 3, 11, 10, 2)
        $g.FillRectangle($dark, 4, 6, 3, 4); $g.FillRectangle($dark, 9, 6, 3, 4)
        $g.FillRectangle($light, 5, 6, 1, 4); $g.FillRectangle($light, 10, 6, 1, 4)
        Save-Png $trapdoor "src\main\resources\assets\gravesown\textures\block\$prefix`_trapdoor.png"
    }
    finally { foreach ($item in @($g,$base,$mid,$dark,$light)) { $item.Dispose() }; $trapdoor.Dispose() }

    $icon = New-Bitmap; $g = [System.Drawing.Graphics]::FromImage($icon)
    $base = Brush $palette.Base; $mid = Brush $palette.Mid; $dark = Brush $palette.Dark; $light = Brush $palette.Light
    $clear = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::Transparent)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        $g.FillRectangle($dark, 3, 1, 10, 14); $g.FillRectangle($base, 4, 2, 8, 12)
        $g.FillRectangle($mid, 5, 3, 6, 2); $g.FillRectangle($dark, 5, 6, 6, 5)
        $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
        $g.FillRectangle($clear, 6, 7, 4, 3)
        $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceOver
        $g.FillRectangle($mid, 7, 7, 1, 3); $g.FillRectangle($light, 10, 11, 1, 1)
        Save-Png $icon "src\main\resources\assets\gravesown\textures\item\$prefix`_door.png"
    }
    finally { foreach ($item in @($g,$base,$mid,$dark,$light,$clear)) { $item.Dispose() }; $icon.Dispose() }
}

function Assert-WoodFamilyModelReferences([string[]]$prefixes) {
    $modelRoot = Join-Path $script:ProjectRoot 'src\main\resources\assets\gravesown\models\block'
    $errors = New-Object System.Collections.Generic.List[string]

    foreach ($prefix in $prefixes) {
        $foreignPrefixes = $prefixes | Where-Object { $_ -ne $prefix }
        foreach ($model in Get-ChildItem -LiteralPath $modelRoot -File -Filter "$prefix`_*.json") {
            $content = [System.IO.File]::ReadAllText($model.FullName)
            foreach ($foreignPrefix in $foreignPrefixes) {
                if ($content.Contains("gravesown:block/$foreignPrefix`_")) {
                    $errors.Add("$($model.Name) references foreign $foreignPrefix block art")
                }
            }

            if ($model.Name -match "^$prefix`_(stairs|slab|fence|fence_gate)" -and
                $content.Contains('"textures"') -and
                -not $content.Contains("gravesown:block/$prefix`_planks")) {
                $errors.Add("$($model.Name) does not reference $prefix`_planks")
            }
        }
    }

    if ($errors.Count -gt 0) {
        throw "Wood-family model reference audit failed:`n$($errors -join "`n")"
    }
}

function Write-TagFile([string]$relativePath, [string[]]$values) {
    $seen = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)
    $ordered = [System.Collections.Generic.List[string]]::new()
    foreach ($value in $values) {
        if ($seen.Add($value)) { $ordered.Add($value) }
    }

    $encoded = @($ordered | ForEach-Object { '    "' + $_ + '"' }) -join ",`n"
    Write-GeneratedText $relativePath "{`n  `"replace`": false,`n  `"values`": [`n$encoded`n  ]`n}"
}

function Write-WoodFamilyTags([object[]]$familyDefinitions) {
    $prefixes = @($familyDefinitions | ForEach-Object { [string]$_.Prefix })
    $normalPlanks = @($familyDefinitions | ForEach-Object { "gravesown:$($_.Planks)" })
    $cutPlanks = @($familyDefinitions | ForEach-Object { "gravesown:$($_.CutPlanks)" })
    $allPlanks = @($normalPlanks + $cutPlanks)
    $foliage = @($familyDefinitions | ForEach-Object { "gravesown:$($_.Foliage)" })
    $shoots = @($familyDefinitions | ForEach-Object { "gravesown:$($_.Shoot)" })
    $logTags = @($prefixes | ForEach-Object { "#gravesown:$($_)_logs" })
    $fences = @($prefixes | ForEach-Object { "gravesown:$($_)_fence" })
    $fenceGates = @($prefixes | ForEach-Object { "gravesown:$($_)_fence_gate" })
    $doors = @($prefixes | ForEach-Object { "gravesown:$($_)_door" })
    $trapdoors = @($prefixes | ForEach-Object { "gravesown:$($_)_trapdoor" })

    foreach ($family in $familyDefinitions) {
        $stem = "gravesown:$($family.Prefix)_stem"
        Write-TagFile "src\main\resources\data\gravesown\tags\block\$($family.Prefix)_logs.json" @($stem)
        Write-TagFile "src\main\resources\data\gravesown\tags\item\$($family.Prefix)_logs.json" @($stem)
    }

    foreach ($kind in @('block','item')) {
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\logs.json" $logTags
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\logs_that_burn.json" $logTags
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\planks.json" $allPlanks
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\leaves.json" $foliage
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\saplings.json" $shoots
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\fences.json" $fences
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\wooden_fences.json" $fences
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\fence_gates.json" $fenceGates
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\doors.json" $doors
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\wooden_doors.json" $doors
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\trapdoors.json" $trapdoors
        Write-TagFile "src\main\resources\data\minecraft\tags\$kind\wooden_trapdoors.json" $trapdoors
        Write-TagFile "src\main\resources\data\gravesown\tags\$kind\planks.json" $allPlanks
        Write-TagFile "src\main\resources\data\gravesown\tags\$kind\cut_planks.json" $cutPlanks
    }

    $axeBlocks = [System.Collections.Generic.List[string]]::new()
    foreach ($prefix in $prefixes) {
        foreach ($suffix in @('stem','planks','cut_planks','stairs','slab','fence','fence_gate','door','trapdoor')) {
            $axeBlocks.Add("gravesown:$prefix`_$suffix")
        }
    }
    foreach ($station in @('gravework_bench','reliquary_crate','field_kitchen','sawmill','tallow_lantern')) {
        $axeBlocks.Add("gravesown:$station")
    }
    Write-TagFile 'src\main\resources\data\minecraft\tags\block\mineable\axe.json' $axeBlocks

    # Tree placement may overwrite low plants, never terrain or an established
    # sapling.  Keeping this list beside the family matrix prevents new biome
    # flora from becoming accidental hard blockers for tree features.
    Write-TagFile 'src\main\resources\data\minecraft\tags\block\replaceable_by_trees.json' @(
        'gravesown:threadgrass',
        'gravesown:pallid_bulb',
        'gravesown:cinder_bloom',
        'gravesown:sinew_fern',
        'gravesown:marrow_reed',
        'gravesown:rift_thorn',
        'gravesown:mire_frond',
        'gravesown:mossveil',
        'gravesown:amber_bloom'
    )
}

function Assert-WoodFamilyTags([object[]]$familyDefinitions) {
    $requiredFiles = @(
        'src\main\resources\data\minecraft\tags\block\logs.json',
        'src\main\resources\data\minecraft\tags\block\logs_that_burn.json',
        'src\main\resources\data\minecraft\tags\block\planks.json',
        'src\main\resources\data\minecraft\tags\block\leaves.json',
        'src\main\resources\data\minecraft\tags\block\saplings.json',
        'src\main\resources\data\minecraft\tags\block\wooden_fences.json',
        'src\main\resources\data\minecraft\tags\block\fence_gates.json',
        'src\main\resources\data\minecraft\tags\block\wooden_doors.json',
        'src\main\resources\data\minecraft\tags\block\wooden_trapdoors.json',
        'src\main\resources\data\minecraft\tags\block\mineable\axe.json',
        'src\main\resources\data\minecraft\tags\item\logs.json',
        'src\main\resources\data\minecraft\tags\item\planks.json',
        'src\main\resources\data\minecraft\tags\item\leaves.json',
        'src\main\resources\data\minecraft\tags\item\saplings.json',
        'src\main\resources\data\minecraft\tags\item\wooden_fences.json',
        'src\main\resources\data\minecraft\tags\item\fence_gates.json',
        'src\main\resources\data\minecraft\tags\item\wooden_doors.json',
        'src\main\resources\data\minecraft\tags\item\wooden_trapdoors.json'
    )
    $errors = [System.Collections.Generic.List[string]]::new()
    foreach ($relative in $requiredFiles) {
        $path = Join-Path $script:ProjectRoot $relative
        if (-not (Test-Path -LiteralPath $path)) {
            $errors.Add("missing tag $relative")
            continue
        }
        $content = [System.IO.File]::ReadAllText($path)
        foreach ($family in $familyDefinitions) {
            $expected = if ($relative -match '[\\/]leaves\.json$') {
                [string]$family.Foliage
            }
            elseif ($relative -match '[\\/]saplings\.json$') {
                [string]$family.Shoot
            }
            else {
                [string]$family.Prefix
            }
            if (-not $content.Contains($expected)) {
                $errors.Add("$relative omits $($family.Prefix)")
            }
        }
    }
    if ($errors.Count -gt 0) { throw "Wood-family tag audit failed:`n$($errors -join "`n")" }
}

$families = @(
    @{
        Prefix='ribroot'; Planks='ribroot_planks'; CutPlanks='ribroot_cut_planks'; Foliage='veil_foliage'; Shoot='ribroot_shoot'
        Palette=@{Base='#5A422F';Mid='#73543A';Dark='#30251D';Light='#9A744B'}
        LeafPalette=@{Base='#52623B';Mid='#687A49';Dark='#303D2C';Light='#8C985F'}
    },
    @{
        Prefix='emberbark'; Planks='emberbark_planks'; CutPlanks='emberbark_cut_planks'; Foliage='emberbark_foliage'; Shoot='emberbark_shoot'
        Palette=@{Base='#8A4C2F';Mid='#A9623B';Dark='#4B2C23';Light='#CD824B'}
        LeafPalette=@{Base='#8A5131';Mid='#A96B3C';Dark='#523329';Light='#D28A4E'}
    },
    @{
        Prefix='palevine'; Planks='palevine_planks'; CutPlanks='palevine_cut_planks'; Foliage='palevine_foliage'; Shoot='palevine_shoot'
        Palette=@{Base='#A49878';Mid='#B7AA84';Dark='#625B49';Light='#D1C49A'}
        LeafPalette=@{Base='#84906A';Mid='#9EAA7E';Dark='#525E47';Light='#C1C99A'}
    },
    @{
        Prefix='cairnwood'; Planks='cairnwood_planks'; CutPlanks='cairnwood_cut_planks'; Foliage='cairnwood_foliage'; Shoot='cairnwood_shoot'
        Palette=@{Base='#766752';Mid='#8E7D62';Dark='#493F34';Light='#B3A17D'}
        LeafPalette=@{Base='#697052';Mid='#818866';Dark='#414836';Light='#A3AA7E'}
    },
    @{
        Prefix='suturewood'; Planks='suturewood_planks'; CutPlanks='suturewood_cut_planks'; Foliage='suturewood_foliage'; Shoot='suturewood_shoot'
        Palette=@{Base='#51432E';Mid='#69583A';Dark='#2F2B21';Light='#8B754A'}
        LeafPalette=@{Base='#3E5C43';Mid='#547458';Dark='#273C2E';Light='#74926C'}
    },
    @{
        Prefix='mosswake'; Planks='mosswake_planks'; CutPlanks='mosswake_cut_planks'; Foliage='mosswake_foliage'; Shoot='mosswake_shoot'
        Palette=@{Base='#6A5B36';Mid='#817346';Dark='#3D3826';Light='#A69A60'}
        LeafPalette=@{Base='#5C7844';Mid='#739255';Dark='#354B31';Light='#98B56D'}
    },
    @{
        Prefix='sunveil'; Planks='sunveil_planks'; CutPlanks='sunveil_cut_planks'; Foliage='sunveil_foliage'; Shoot='sunveil_shoot'
        Palette=@{Base='#987744';Mid='#B18E54';Dark='#5D492F';Light='#D1B573'}
        LeafPalette=@{Base='#70834B';Mid='#8CA25A';Dark='#465735';Light='#B2C777'}
    }
)

foreach ($family in $families) {
    $prefix = $family.Prefix; $palette = $family.Palette
    if ($prefix -ne 'ribroot') {
        Copy-TemplateFamily $prefix
        Copy-CutPlankFamily $prefix
    }
    Save-WoodTile $family.Planks $palette
    Save-CutWoodTile $family.CutPlanks $palette
    Save-Stem $prefix $palette
    Save-Cutout $family.Foliage $family.LeafPalette $true
    Save-Cutout $family.Shoot $family.LeafPalette $false
    Save-DoorArt $prefix $palette
}

Write-WoodFamilyTags $families
Assert-WoodFamilyModelReferences @($families | ForEach-Object { $_.Prefix })
Assert-WoodFamilyTags $families
Write-Host "PASS generated $($families.Count) species-specific wood palettes, full resource families and tag matrix."
