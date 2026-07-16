Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot

# One stable entry point for a fresh agent/session.  The order is explicit because
# a few historical generators still emit bootstrap versions of assets later refined
# by a family pass.  Alphabetical discovery silently restored old gray/brick terrain
# and flat creature atlases.  Art Language V3 is deliberately last: it is the
# binding shipped-art owner and records direct/UV-safe coverage for every PNG.
$generatorNames = @(
    'generate-presentation-background.ps1',
    'generate-dev-art.ps1',
    'generate-aquatic-art.ps1',
    'generate-farming-decor-art.ps1',
    'generate-flora-art.ps1',
    'generate-guide-art.ps1',
    'generate-kitchen-art.ps1',
    'generate-remnant-art.ps1',
    'generate-ribspring-art.ps1',
    'generate-stitchtusk-art.ps1',
    'generate-survival-stations-art.ps1',
    'generate-symmetric-quietskin-art.ps1',
    'generate-wood-family-art.ps1',
    'generate-ui-art.ps1',
    'generate-cohesive-world-art.ps1',
    'generate-cohesive-creature-art.ps1',
    'generate-native-fauna-art.ps1',
    'generate-art-language-v3.ps1'
)

$generators = foreach ($name in $generatorNames) {
    $path = Join-Path $PSScriptRoot $name
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Required deterministic art generator is missing: $name"
    }
    Get-Item -LiteralPath $path
}

foreach ($generator in $generators) {
    Write-Step "Generating art with $($generator.Name)"
    & $generator.FullName
}

Write-Host ''
Write-Host "All deterministic art generators finished: $($generators.Count) scripts" -ForegroundColor Green
