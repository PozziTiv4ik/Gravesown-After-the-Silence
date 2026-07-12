. "$PSScriptRoot\common.ps1"

Enter-ProjectRoot
$javaHome = Use-ProjectJava
Write-Host "Using JAVA_HOME=$javaHome"
Write-Step 'Launching Gravesown development client'
Invoke-ProjectGradle 'runClient'
