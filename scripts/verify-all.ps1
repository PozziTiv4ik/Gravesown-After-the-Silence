[CmdletBinding()]
param(
    [switch]$FullWorld,
    # Kept for compatibility. Strict world verification is now the default.
    [switch]$StrictWorld,
    [switch]$BaselineWorld
)

. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot
if ($StrictWorld -and $BaselineWorld) {
    throw '-StrictWorld and -BaselineWorld cannot be used together.'
}
Write-Step '1/5 Environment and compile doctor'
& "$PSScriptRoot\doctor.ps1"

Write-Step '2/5 Deterministic art contract'
& "$PSScriptRoot\verify-art.ps1"

Write-Step '3/5 Server GameTests'
& "$PSScriptRoot\run-gametests.ps1"

Write-Step '4/5 Clean release build'
& "$PSScriptRoot\build.ps1"

Write-Step '5/5 Real generated-chunk world audit'
$profile = if ($FullWorld) { 'Full' } else { 'Smoke' }
& "$PSScriptRoot\run-worldtest.ps1" -Profile $profile -Strict:$StrictWorld -Baseline:$BaselineWorld

Write-Host ''
Write-Host 'VERIFY-ALL PASS' -ForegroundColor Green
