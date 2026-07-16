. "$PSScriptRoot\common.ps1"

Enter-ProjectRoot
$javaHome = Use-ProjectJava
Write-Host "Using JAVA_HOME=$javaHome"
# FML's immediate window is created before any mod resources or classes exist.
# Its supported dark scheme avoids the loader's bright red fallback until the
# Gravesown resource-backed loading screens can take over.
$env:FML_EARLY_WINDOW_DARK = '1'
Write-Step 'Launching Gravesown development client'
Invoke-ProjectGradle 'runClient' '--offline' '--no-daemon'
