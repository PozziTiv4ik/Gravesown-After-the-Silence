param(
    [switch]$SkipCompile
)

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot

Write-Step 'Checking Java 21'
$javaHome = Use-ProjectJava
Write-Host "PASS Java: $javaHome" -ForegroundColor Green
Write-Host (Get-JavaVersionText -JavaExecutable (Join-Path $javaHome 'bin\java.exe'))

Write-Step 'Checking pinned project versions'
$properties = Get-Content -Raw -LiteralPath (Join-Path $script:ProjectRoot 'gradle.properties')
$required = @(
    'minecraft_version=1.21.1',
    'neo_version=21.1.235',
    'mod_id=gravesown',
    'mod_group_id=dev.gravesown'
)
foreach ($line in $required) {
    if (-not $properties.Contains($line)) {
        throw "Missing pinned property: $line"
    }
    Write-Host "PASS $line" -ForegroundColor Green
}

$requiredFiles = @(
    'gradlew.bat',
    'gradle\wrapper\gradle-wrapper.jar',
    'src\main\templates\META-INF\neoforge.mods.toml',
    'AGENTS.md',
    'docs\STATUS.md'
)
foreach ($relative in $requiredFiles) {
    $path = Join-Path $script:ProjectRoot $relative
    if (-not (Test-Path -LiteralPath $path)) {
        throw "Missing required file: $relative"
    }
    Write-Host "PASS $relative" -ForegroundColor Green
}

$rootPath = [System.IO.Path]::GetPathRoot($script:ProjectRoot)
$driveName = $rootPath.TrimEnd('\').TrimEnd(':')
$drive = Get-PSDrive -Name $driveName
$freeGb = [Math]::Round($drive.Free / 1GB, 1)
Write-Host "INFO Free disk space: $freeGb GB"
if ($freeGb -lt 6) {
    Write-Warning 'Less than 6 GB free; first NeoForge setup may fail.'
}

Write-Step 'Checking Gradle'
Invoke-ProjectGradle '--version'

if (-not $SkipCompile) {
    Write-Step 'Compiling Java sources'
    Invoke-ProjectGradle 'compileJava'
}

Write-Host ''
Write-Host 'Doctor finished: PASS' -ForegroundColor Green
