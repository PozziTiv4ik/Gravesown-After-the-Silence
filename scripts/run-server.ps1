. "$PSScriptRoot\common.ps1"

Enter-ProjectRoot
$javaHome = Use-ProjectJava
Write-Host "Using JAVA_HOME=$javaHome"

$eulaPath = Join-Path $script:ProjectRoot 'run\eula.txt'
if ((Test-Path -LiteralPath $eulaPath) -and
    -not (Select-String -LiteralPath $eulaPath -SimpleMatch 'eula=true' -Quiet)) {
    Write-Warning 'The server EULA is not accepted.'
    Write-Host "Read $eulaPath and change eula=false to eula=true only if you accept it."
}

Write-Step 'Launching Gravesown dedicated development server'
Invoke-ProjectGradle 'runServer'
