. "$PSScriptRoot\common.ps1"

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Enter-ProjectRoot
& "$PSScriptRoot\verify-github-readiness.ps1"

$properties = @{}
Get-Content -LiteralPath (Join-Path $script:ProjectRoot 'gradle.properties') -Encoding UTF8 |
    Where-Object { $_ -match '^[^#=]+=' } |
    ForEach-Object {
        $key, $value = $_ -split '=', 2
        $properties[$key.Trim()] = $value.Trim()
    }
$version = $properties.mod_version
if ([string]::IsNullOrWhiteSpace($version)) {
    throw 'mod_version is missing from gradle.properties.'
}

$releaseRoot = Join-Path $script:ProjectRoot 'release'
$stageRoot = Join-Path $releaseRoot 'stage'
$bundleRoot = Join-Path $stageRoot 'Gravesown After the Silence'
$zipPath = Join-Path $releaseRoot "Gravesown-After-the-Silence-$version-Windows.zip"

if (Test-Path -LiteralPath $stageRoot) {
    $resolved = (Resolve-Path -LiteralPath $stageRoot).Path
    if (-not (Test-PathInside -Parent $script:ProjectRoot -Child $resolved)) {
        throw "Unsafe release staging path: $resolved"
    }
    Remove-Item -LiteralPath $resolved -Recurse -Force
}
if (Test-Path -LiteralPath $zipPath) {
    Remove-Item -LiteralPath $zipPath -Force
}
New-Item -ItemType Directory -Force -Path $bundleRoot | Out-Null

$repositoryFiles = @(
    & git ls-files --cached --others --exclude-standard |
        Sort-Object -Unique
)
if ($LASTEXITCODE -ne 0) {
    throw 'git ls-files failed while preparing the release bundle.'
}
foreach ($relative in $repositoryFiles) {
    $source = Join-Path $script:ProjectRoot $relative
    if (-not (Test-Path -LiteralPath $source -PathType Leaf)) {
        continue
    }
    $destination = Join-Path $bundleRoot $relative
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $destination) | Out-Null
    Copy-Item -LiteralPath $source -Destination $destination -Force
}

$launcherImage = Join-Path $script:ProjectRoot 'launcher\dist\Gravesown Launcher'
$releaseJar = Join-Path $script:ProjectRoot "dist\gravesown-$version.jar"
if (-not (Test-Path -LiteralPath (Join-Path $launcherImage 'Gravesown Launcher.exe'))) {
    throw 'Packaged Windows launcher is missing. Run launcher\build-launcher.cmd.'
}
if (-not (Test-Path -LiteralPath $releaseJar -PathType Leaf)) {
    throw "Release JAR is missing: $releaseJar"
}
$launcherDist = Join-Path $bundleRoot 'launcher\dist'
New-Item -ItemType Directory -Force -Path $launcherDist | Out-Null
Copy-Item -LiteralPath $launcherImage -Destination $launcherDist -Recurse -Force
New-Item -ItemType Directory -Force -Path (Join-Path $bundleRoot 'dist') | Out-Null
Copy-Item -LiteralPath $releaseJar -Destination (Join-Path $bundleRoot 'dist') -Force

$notice = @'
GRAVESOWN: AFTER THE SILENCE

ЗАПУСК НА WINDOWS
1. Запусти launcher.cmd.
2. При первом запуске setup.cmd подготовит Java 21 и официальные зависимости
   Minecraft/NeoForge в профиле пользователя.
3. Следующие запуски работают офлайн и используют общий внешний кэш.

Minecraft не распространяется внутри этого архива. Соблюдай Minecraft EULA и
используй необходимую лицензию игры.

Миры игрока, логи, Java и кэши хранятся вне этой папки:
%LOCALAPPDATA%\Gravesown

WINDOWS START
1. Run launcher.cmd.
2. On the first launch, setup.cmd prepares Java 21 and the official
   Minecraft/NeoForge dependencies in your user profile.
3. Later launches are offline and reuse the external cache.

Minecraft game binaries are not redistributed in this archive. You must comply
with the Minecraft EULA and own any required game license.

Player worlds, logs, Java and caches are stored outside this folder under:
%LOCALAPPDATA%\Gravesown
'@
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Join-Path $bundleRoot 'START_HERE.txt'), $notice, $utf8NoBom)

Compress-Archive -LiteralPath $bundleRoot -DestinationPath $zipPath -CompressionLevel Optimal
Remove-Item -LiteralPath $stageRoot -Recurse -Force

$zip = Get-Item -LiteralPath $zipPath
Write-Host ('RELEASE PACKAGE PASS: {0} ({1:N1} MiB)' -f $zip.FullName, ($zip.Length / 1MB)) -ForegroundColor Green
