. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot

if (-not (Test-GravesownSetupReady)) {
    Write-Step 'First launch: preparing Java, NeoForge and Minecraft dependencies'
    & "$PSScriptRoot\setup.ps1"
    if ($LASTEXITCODE -and $LASTEXITCODE -ne 0) {
        throw "Initial setup failed with exit code $LASTEXITCODE."
    }
    if (-not (Test-GravesownSetupReady)) {
        throw 'Initial setup finished without creating a valid setup marker.'
    }
}

& "$PSScriptRoot\run-client.ps1"
if ($LASTEXITCODE -and $LASTEXITCODE -ne 0) {
    throw "Client failed with exit code $LASTEXITCODE."
}
