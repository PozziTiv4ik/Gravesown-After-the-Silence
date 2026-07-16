Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"

$projectRoot = $script:ProjectRoot.TrimEnd('\')
$dataRoot = $script:GravesownHome.TrimEnd('\')
$targets = @(
    @{ Parent = $projectRoot; Path = (Join-Path $projectRoot '.tools\gradle-user-home') },
    @{ Parent = $projectRoot; Path = (Join-Path $projectRoot '.gradle-user-home') },
    @{ Parent = $projectRoot; Path = (Join-Path $projectRoot '.gradle') },
    @{ Parent = $projectRoot; Path = (Join-Path $projectRoot 'run-clienttest') },
    @{ Parent = $projectRoot; Path = (Join-Path $projectRoot 'run-worldtest') },
    @{ Parent = $dataRoot; Path = (Get-GravesownPath 'runs\tests') },
    @{ Parent = $dataRoot; Path = (Get-GravesownPath 'cache\project-gradle') },
    @{ Parent = $dataRoot; Path = $script:ProjectGradleCache }
)

$runningProjectJava = Get-CimInstance Win32_Process |
    Where-Object {
        $_.Name -in @('java.exe', 'javaw.exe') -and
        $_.CommandLine -and
        ($_.CommandLine.Contains($projectRoot, [System.StringComparison]::OrdinalIgnoreCase) -or
         $_.CommandLine.Contains($dataRoot, [System.StringComparison]::OrdinalIgnoreCase))
    }

if ($runningProjectJava) {
    $ids = ($runningProjectJava.ProcessId -join ', ')
    throw "Project Java processes are still running: $ids. Close them before cleanup."
}

$resolvedTargets = foreach ($target in $targets) {
    $candidate = $target.Path
    if (-not (Test-Path -LiteralPath $candidate)) {
        continue
    }

    $resolved = (Resolve-Path -LiteralPath $candidate).Path
    $parent = [System.IO.Path]::GetFullPath($target.Parent).TrimEnd('\')
    if (-not $resolved.StartsWith("$parent\", [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Unsafe cleanup target outside its owned root $parent`: $resolved"
    }
    if ($resolved -eq $projectRoot -or $resolved -eq $dataRoot) {
        throw 'Refusing to delete a storage root.'
    }

    $size = (Get-ChildItem -LiteralPath $resolved -Recurse -Force -File -ErrorAction SilentlyContinue |
        Measure-Object -Property Length -Sum).Sum
    [pscustomobject]@{
        Path = $resolved
        Bytes = [long]$size
    }
}

if (-not $resolvedTargets) {
    Write-Host 'Nothing to clean.' -ForegroundColor Green
    exit 0
}

$totalBytes = ($resolvedTargets | Measure-Object -Property Bytes -Sum).Sum
foreach ($target in $resolvedTargets) {
    Write-Host ("Removing {0} ({1:N1} MiB)" -f $target.Path, ($target.Bytes / 1MB))
    Remove-Item -LiteralPath $target.Path -Recurse -Force
}

Write-Host ''
Write-Host ("Cleanup complete: freed approximately {0:N2} GiB." -f ($totalBytes / 1GB)) -ForegroundColor Green
Write-Host 'Preserved external Java, shared Gradle dependencies, player worlds, source files, launcher, JARs and reports.' -ForegroundColor Green
