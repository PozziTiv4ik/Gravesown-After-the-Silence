. "$PSScriptRoot\common.ps1"

Enter-ProjectRoot
$javaHome = Use-ProjectJava
Write-Host "Using JAVA_HOME=$javaHome"

Write-Step 'Running checks and building Gravesown'
Invoke-ProjectGradle 'clean' 'check' 'build'

$releaseJar = Get-ReleaseJar
$distDir = Join-Path $script:ProjectRoot 'dist'
New-Item -ItemType Directory -Force -Path $distDir | Out-Null
$target = Join-Path $distDir $releaseJar.Name
Copy-Item -LiteralPath $releaseJar.FullName -Destination $target -Force

Write-Host ''
Write-Host 'BUILD PASS' -ForegroundColor Green
Write-Host "Release JAR: $target" -ForegroundColor Green
