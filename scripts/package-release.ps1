. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot
& "$PSScriptRoot\build.ps1"
& (Join-Path $script:ProjectRoot 'launcher\build-launcher.ps1')
& "$PSScriptRoot\package-github-release.ps1"
& "$PSScriptRoot\verify-release-bundle.ps1"
