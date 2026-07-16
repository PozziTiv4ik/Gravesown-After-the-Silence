. "$PSScriptRoot\common.ps1"

Enter-ProjectRoot
$javaHome = Use-ProjectJava
Write-Host "Using JAVA_HOME=$javaHome"

Write-Step 'Running Gravesown automated NeoForge GameTests'
Invoke-ProjectGradle 'runGameTestServer' '--offline' '--no-daemon'

Write-Host ''
Write-Host 'GAMETEST PASS' -ForegroundColor Green
