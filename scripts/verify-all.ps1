[CmdletBinding()]
param(
    [switch]$FullWorld,
    [switch]$StrictWorld
)

. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot
Write-Step '1/4 Environment and compile doctor'
& "$PSScriptRoot\doctor.ps1"

Write-Step '2/4 Server GameTests'
& "$PSScriptRoot\run-gametests.ps1"

Write-Step '3/4 Clean release build'
& "$PSScriptRoot\build.ps1"

Write-Step '4/4 Real generated-chunk world audit'
$profile = if ($FullWorld) { 'Full' } else { 'Smoke' }
& "$PSScriptRoot\run-worldtest.ps1" -Profile $profile -Strict:$StrictWorld

Write-Host ''
Write-Host 'VERIFY-ALL PASS' -ForegroundColor Green
