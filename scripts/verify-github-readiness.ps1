. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot
Write-Step 'Checking GitHub repository contents'

$required = @(
    'AGENTS.md',
    'README.md',
    'LICENSE',
    'gradlew.bat',
    'gradle\wrapper\gradle-wrapper.jar',
    'launcher.cmd',
    'play.cmd',
    'setup.cmd',
    'scripts\bootstrap-and-play.ps1',
    'src\main\java\dev\gravesown\Gravesown.java'
)
foreach ($relative in $required) {
    if (-not (Test-Path -LiteralPath (Join-Path $script:ProjectRoot $relative) -PathType Leaf)) {
        throw "Required repository file is missing: $relative"
    }
}

$candidatePaths = @(
    & git ls-files --cached --others --exclude-standard |
        ForEach-Object { $_.Replace('\', '/') } |
        Sort-Object -Unique
)
if ($LASTEXITCODE -ne 0) {
    throw 'git ls-files failed.'
}

$forbiddenPatterns = @(
    '^\.tools/',
    '^\.gradle(?:-user-home)?/',
    '^build/',
    '^dist/',
    '^release/',
    '^run(?:-|/)',
    '^launcher/(?:build|build-verify|dist)/',
    '(?:^|/)(?:eula\.txt|servers\.dat|launcher_accounts\.json|usercache\.json|usernamecache\.json)$',
    '\.(?:class|lck|part|pem|pfx|p12)$',
    '(?:^|/)\.env(?:\.|$)'
)
$forbidden = foreach ($path in $candidatePaths) {
    if ($forbiddenPatterns | Where-Object { $path -match $_ }) {
        $path
    }
}
if ($forbidden) {
    throw "Forbidden generated, cache, world or credential files would enter Git:`n$($forbidden -join "`n")"
}

$existingFiles = foreach ($relative in $candidatePaths) {
    $path = Join-Path $script:ProjectRoot $relative
    if (Test-Path -LiteralPath $path -PathType Leaf) {
        Get-Item -LiteralPath $path
    }
}
$tooLarge = @($existingFiles | Where-Object Length -ge 95MB)
if ($tooLarge) {
    $details = $tooLarge | ForEach-Object {
        '{0:N1} MiB  {1}' -f ($_.Length / 1MB), $_.FullName
    }
    throw "GitHub-blocking files at or above 95 MiB:`n$($details -join "`n")"
}

$largeWarnings = @($existingFiles | Where-Object { $_.Length -ge 50MB -and $_.Length -lt 95MB })
foreach ($file in $largeWarnings) {
    Write-Warning ('Large Git candidate: {0:N1} MiB  {1}' -f ($file.Length / 1MB), $file.FullName)
}

& git diff --check
if ($LASTEXITCODE -ne 0) {
    throw 'git diff --check failed.'
}

$candidateBytes = ($existingFiles | Measure-Object -Property Length -Sum).Sum
$trackedCount = @(& git ls-files).Count
$remote = @(& git remote -v)

Write-Host ('PASS Candidate files: {0}, total {1:N1} MiB' -f $candidatePaths.Count, ($candidateBytes / 1MB)) -ForegroundColor Green
Write-Host "PASS Tracked files: $trackedCount" -ForegroundColor Green
Write-Host "PASS Runtime outside repository: $script:GravesownHome" -ForegroundColor Green
Write-Host "PASS Gradle user cache outside repository: $script:ExternalGradleUserHome" -ForegroundColor Green
if ($remote.Count -eq 0) {
    Write-Host 'INFO No Git remote is configured yet; publication has not been attempted.' -ForegroundColor Yellow
}
else {
    $remote | ForEach-Object { Write-Host "INFO Remote: $_" }
}
Write-Host 'GITHUB READINESS: PASS' -ForegroundColor Green
