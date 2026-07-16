. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot
$javaHome = Use-ProjectJava
Write-Host "Using JAVA_HOME=$javaHome"

$sourceRoot = Get-GravesownRunPath 'tests\worldtest'
$sourceSentinel = Join-Path $sourceRoot '.gravesown-worldtest-root'
$sourceWorld = [System.IO.Path]::GetFullPath((Join-Path $sourceRoot 'world-audit'))
$runRoot = Get-GravesownRunPath 'tests\client-smoke'
$sentinel = Join-Path $runRoot '.gravesown-clienttest-root'
$saveRoot = [System.IO.Path]::GetFullPath((Join-Path $runRoot 'saves'))
$saveName = 'tc4-client-smoke'
$savePath = [System.IO.Path]::GetFullPath((Join-Path $saveRoot $saveName))
$latestLog = Join-Path $runRoot 'logs\latest.log'
$codexScreenshot = Join-Path $runRoot 'screenshots\gravesown-codex-smoke.png'
$guideScreenshot = Join-Path $runRoot 'screenshots\gravesown-guide-smoke.png'
$creativeScreenshot = Join-Path $runRoot 'screenshots\gravesown-creative-inventory-smoke.png'
$entityScreenshot = Join-Path $runRoot 'screenshots\gravesown-creature-lineup-smoke.png'
$armorScreenshot = Join-Path $runRoot 'screenshots\gravesown-quietskin-smoke.png'
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)

function Assert-ChildPath {
    param(
        [Parameter(Mandatory = $true)][string]$Parent,
        [Parameter(Mandatory = $true)][string]$Child,
        [Parameter(Mandatory = $true)][string]$Label
    )

    $parentPrefix = [System.IO.Path]::GetFullPath($Parent).TrimEnd('\') + '\'
    $resolvedChild = [System.IO.Path]::GetFullPath($Child)
    if (-not $resolvedChild.StartsWith($parentPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Refusing $Label outside $Parent`: $resolvedChild"
    }
}

function Initialize-SafeClientTestRoot {
    Assert-ChildPath -Parent $script:GravesownHome -Child $runRoot -Label 'a client-test root'
    if (-not (Test-Path -LiteralPath $runRoot)) {
        New-Item -ItemType Directory -Path $runRoot | Out-Null
    }

    if (-not (Test-Path -LiteralPath $sentinel)) {
        $existing = @(Get-ChildItem -LiteralPath $runRoot -Force -ErrorAction Stop)
        if ($existing.Count -gt 0) {
            throw "Refusing to use $runRoot because its Gravesown sentinel is missing and the directory is not empty."
        }
        [System.IO.File]::WriteAllText(
            $sentinel,
            "Owned by Gravesown clienttest.cmd. Never store personal worlds here.`r`n",
            $utf8NoBom
        )
    }
}

function Remove-SafeClientSmokeSave {
    Assert-ChildPath -Parent $runRoot -Child $saveRoot -Label 'a client-test saves root'
    Assert-ChildPath -Parent $saveRoot -Child $savePath -Label 'a client smoke save'
    if ((Split-Path -Leaf $savePath) -ne $saveName) {
        throw "Refusing unexpected client smoke save path: $savePath"
    }
    if (-not (Test-Path -LiteralPath $sentinel)) {
        throw "Refusing to remove a client smoke save without sentinel: $sentinel"
    }
    if (Test-Path -LiteralPath $savePath) {
        $runRootItem = Get-Item -LiteralPath $runRoot -Force
        $saveItem = Get-Item -LiteralPath $savePath -Force
        if (($runRootItem.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -ne 0 -or
            ($saveItem.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -ne 0) {
            throw 'Refusing to recursively remove a client-test path containing a junction or symbolic link.'
        }
        Remove-Item -LiteralPath $savePath -Recurse -Force
    }
}

Write-Step 'Preparing a fresh fixed-seed strict audit world for the client'
& "$PSScriptRoot\run-worldtest.ps1" -Profile Smoke

if (-not (Test-Path -LiteralPath $sourceSentinel)) {
    throw 'The isolated world-test sentinel is missing after the Smoke audit.'
}
if (-not (Test-Path -LiteralPath (Join-Path $sourceWorld 'level.dat'))) {
    throw 'The isolated audit world is missing after the Smoke audit.'
}
Assert-ChildPath -Parent $sourceRoot -Child $sourceWorld -Label 'a world-test source save'

Initialize-SafeClientTestRoot
Remove-SafeClientSmokeSave
if (-not (Test-Path -LiteralPath $saveRoot)) {
    New-Item -ItemType Directory -Path $saveRoot | Out-Null
}
Copy-Item -LiteralPath $sourceWorld -Destination $savePath -Recurse

if (Test-Path -LiteralPath $latestLog) {
    Remove-Item -LiteralPath $latestLog -Force
}
foreach ($screenshot in @($codexScreenshot, $guideScreenshot, $creativeScreenshot, $entityScreenshot, $armorScreenshot)) {
    if (Test-Path -LiteralPath $screenshot) {
        Remove-Item -LiteralPath $screenshot -Force
    }
}

Write-Step 'Running isolated Gravesown integrated-client smoke test'
Invoke-ProjectGradle 'runClientSmoke' '--offline' '--no-daemon'

if (-not (Test-Path -LiteralPath $latestLog)) {
    throw "Client exited without writing $latestLog"
}
$log = Get-Content -LiteralPath $latestLog -Raw -Encoding UTF8
if ($log -notmatch 'GRAVESOWN_CLIENT_SMOKE_RESULT status=PASS') {
    $failure = [regex]::Match($log, 'GRAVESOWN_CLIENT_SMOKE_RESULT status=FAIL[^\r\n]*')
    if ($failure.Success) {
        throw "Client smoke failed: $($failure.Value)"
    }
    throw 'Client exited without a Gravesown client-smoke PASS marker.'
}
foreach ($requiredMarker in @(
    'GRAVESOWN_CODEX_AUTO_GRANT verified=true count=1 attachment=true',
    'GRAVESOWN_RECIPE_GUIDE verified=true recipes_at_least=33 exact_grid=true search=true categories=true guide=true graph_nodes=6 graph_edges=5 shortcut_request=true',
    'GRAVESOWN_ENTITY_TRACKING verified=true count=14'
)) {
    if (-not $log.Contains($requiredMarker)) {
        throw "Client smoke passed without required evidence marker: $requiredMarker"
    }
}
foreach ($capture in @(
    @{ Label = 'Codex'; Path = $codexScreenshot },
    @{ Label = 'Guide'; Path = $guideScreenshot },
    @{ Label = 'Creative survival inventory'; Path = $creativeScreenshot },
    @{ Label = 'creature lineup'; Path = $entityScreenshot },
    @{ Label = 'Quietskin'; Path = $armorScreenshot }
)) {
    if (-not (Test-Path -LiteralPath $capture.Path)) {
        throw "Client smoke passed without writing the $($capture.Label) visual capture: $($capture.Path)"
    }
    if ((Get-Item -LiteralPath $capture.Path).Length -le 0) {
        throw "Client smoke wrote an empty $($capture.Label) visual capture: $($capture.Path)"
    }
    Add-Type -AssemblyName System.Drawing
    $image = [System.Drawing.Image]::FromFile($capture.Path)
    try {
        if ($image.Width -ne 1920 -or $image.Height -ne 1080) {
            throw "The $($capture.Label) capture is $($image.Width)x$($image.Height); expected 1920x1080."
        }
    }
    finally {
        $image.Dispose()
    }
}

Write-Host ''
Write-Host 'CLIENTTEST PASS: the disposable custom-preset world loaded on the integrated server.' -ForegroundColor Green
Write-Host "Codex capture: $codexScreenshot"
Write-Host "Guide capture: $guideScreenshot"
Write-Host "Creative capture: $creativeScreenshot"
Write-Host "Creature capture: $entityScreenshot"
Write-Host "Armor capture: $armorScreenshot"
