. "$PSScriptRoot\common.ps1"

Enter-ProjectRoot
$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$diagnosticsRoot = Join-Path $script:ProjectRoot 'diagnostics'
$workDir = Join-Path $diagnosticsRoot "gravesown-$timestamp"
$zipPath = "$workDir.zip"
New-Item -ItemType Directory -Force -Path $workDir | Out-Null

Write-Step 'Collecting safe project diagnostics'

try {
    & (Join-Path $PSScriptRoot 'doctor.ps1') *>&1 |
        Out-File -LiteralPath (Join-Path $workDir 'doctor.txt') -Encoding utf8
}
catch {
    $_ | Out-String | Out-File -LiteralPath (Join-Path $workDir 'doctor-error.txt') -Encoding utf8
}

$safeFiles = @(
    @{ Source = 'run\logs\latest.log'; Destination = 'latest.log' },
    @{ Source = 'run\logs\debug.log'; Destination = 'debug.log' },
    @{ Source = 'docs\STATUS.md'; Destination = 'STATUS.md' },
    @{ Source = 'gradle.properties'; Destination = 'gradle.properties' }
)
foreach ($entry in $safeFiles) {
    $source = Join-Path $script:ProjectRoot $entry.Source
    if (Test-Path -LiteralPath $source) {
        Copy-Item -LiteralPath $source -Destination (Join-Path $workDir $entry.Destination)
    }
}

$crashDir = Join-Path $script:ProjectRoot 'run\crash-reports'
if (Test-Path -LiteralPath $crashDir) {
    $latestCrash = Get-ChildItem -LiteralPath $crashDir -File |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1
    if ($latestCrash) {
        Copy-Item -LiteralPath $latestCrash.FullName -Destination (Join-Path $workDir $latestCrash.Name)
    }
}

git status --short --branch |
    Out-File -LiteralPath (Join-Path $workDir 'git-status.txt') -Encoding utf8

Compress-Archive -LiteralPath $workDir -DestinationPath $zipPath -Force
Write-Host "Diagnostics ready: $zipPath" -ForegroundColor Green
Write-Host 'The archive excludes worlds, accounts, options and personal resource packs.'
