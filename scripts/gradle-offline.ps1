. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot

if (-not (Test-GravesownSetupReady)) {
    throw 'Gravesown is not prepared. Run setup.cmd once before using offline Gradle.'
}

$javaHome = Use-ProjectJava
Write-Host "Using JAVA_HOME=$javaHome"

$arguments = @($args) + @('--offline', '--no-daemon')
Invoke-ProjectGradle @arguments
