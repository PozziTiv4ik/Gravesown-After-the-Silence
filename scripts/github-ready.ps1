. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot

Write-Step '1/4 Environment and offline compile'
& "$PSScriptRoot\doctor.ps1"

Write-Step '2/4 Clean offline release build'
& "$PSScriptRoot\build.ps1"

Write-Step '3/4 Windows launcher build and diagnostic'
& (Join-Path $script:ProjectRoot 'launcher\build-launcher.ps1')
$launcherExe = Join-Path $script:ProjectRoot 'launcher\dist\Gravesown Launcher\Gravesown Launcher.exe'
$launcherDiagnostic = Start-Process -FilePath $launcherExe -ArgumentList '--diagnose' `
    -WindowStyle Hidden -Wait -PassThru
if ($launcherDiagnostic.ExitCode -ne 0) {
    throw "Launcher diagnostic failed with exit code $($launcherDiagnostic.ExitCode)."
}
Write-Host 'PASS Packaged launcher diagnostic' -ForegroundColor Green

Write-Step '4/4 GitHub content safety'
& "$PSScriptRoot\verify-github-readiness.ps1"

Write-Host ''
Write-Host 'GITHUB-READY PASS' -ForegroundColor Green
