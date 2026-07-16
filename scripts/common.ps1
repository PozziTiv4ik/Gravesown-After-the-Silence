Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$script:ProjectRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path

function Test-PathInside {
    param(
        [Parameter(Mandatory = $true)][string]$Parent,
        [Parameter(Mandatory = $true)][string]$Child
    )

    $parentPath = [System.IO.Path]::GetFullPath($Parent).TrimEnd('\')
    $childPath = [System.IO.Path]::GetFullPath($Child)
    return $childPath.StartsWith(
        "$parentPath\",
        [System.StringComparison]::OrdinalIgnoreCase
    )
}

function Get-GravesownHome {
    $configured = $env:GRAVESOWN_HOME
    if (-not [string]::IsNullOrWhiteSpace($configured)) {
        $dataPath = [System.IO.Path]::GetFullPath($configured)
    }
    elseif (-not [string]::IsNullOrWhiteSpace($env:LOCALAPPDATA)) {
        $dataPath = Join-Path $env:LOCALAPPDATA 'Gravesown'
    }
    elseif (-not [string]::IsNullOrWhiteSpace($env:USERPROFILE)) {
        $dataPath = Join-Path $env:USERPROFILE '.gravesown'
    }
    else {
        throw 'Cannot determine an external Gravesown data directory.'
    }

    if ($dataPath -eq $script:ProjectRoot -or
        (Test-PathInside -Parent $script:ProjectRoot -Child $dataPath)) {
        throw "GRAVESOWN_HOME must stay outside the Git workspace: $dataPath"
    }

    return [System.IO.Path]::GetFullPath($dataPath)
}

function Get-GravesownPath {
    param([Parameter(Mandatory = $true)][string]$RelativePath)

    return [System.IO.Path]::GetFullPath((Join-Path $script:GravesownHome $RelativePath))
}

function Get-GravesownRunPath {
    param([Parameter(Mandatory = $true)][string]$Name)

    return Get-GravesownPath (Join-Path 'runs' $Name)
}

function Get-GravesownSetupMarker {
    return Get-GravesownPath 'state\setup-v1.json'
}

function Test-GravesownSetupReady {
    return Test-Path -LiteralPath (Get-GravesownSetupMarker) -PathType Leaf
}

function Write-GravesownSetupState {
    $marker = Get-GravesownSetupMarker
    $markerDirectory = Split-Path -Parent $marker
    New-Item -ItemType Directory -Force -Path $markerDirectory | Out-Null
    $state = [ordered]@{
        schema = 1
        project = 'gravesown'
        minecraft = '1.21.1'
        neoforge = '21.1.235'
        preparedUtc = [DateTime]::UtcNow.ToString('o')
        projectRoot = $script:ProjectRoot
        gravesownHome = $script:GravesownHome
        gradleUserHome = $script:ExternalGradleUserHome
    }
    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText(
        $marker,
        ($state | ConvertTo-Json -Depth 3),
        $utf8NoBom
    )
    return $marker
}

function Get-ExternalGradleUserHome {
    if (-not [string]::IsNullOrWhiteSpace($env:GRADLE_USER_HOME)) {
        $cachePath = [System.IO.Path]::GetFullPath($env:GRADLE_USER_HOME)
    }
    elseif (-not [string]::IsNullOrWhiteSpace($env:USERPROFILE)) {
        # Reuse Gradle's normal per-user cache. This avoids duplicate dependency
        # downloads while keeping every downloaded artifact outside the repository.
        $cachePath = Join-Path $env:USERPROFILE '.gradle'
    }
    else {
        $cachePath = Get-GravesownPath 'cache\gradle-user-home'
    }

    if ($cachePath -eq $script:ProjectRoot -or
        (Test-PathInside -Parent $script:ProjectRoot -Child $cachePath)) {
        throw "GRADLE_USER_HOME must stay outside the Git workspace: $cachePath"
    }

    return [System.IO.Path]::GetFullPath($cachePath)
}

function Get-ProjectCacheKey {
    $sha256 = [System.Security.Cryptography.SHA256]::Create()
    try {
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($script:ProjectRoot.ToLowerInvariant())
        $hash = $sha256.ComputeHash($bytes)
        return ([System.BitConverter]::ToString($hash).Replace('-', '').Substring(0, 16)).ToLowerInvariant()
    }
    finally {
        $sha256.Dispose()
    }
}

$script:GravesownHome = Get-GravesownHome
$script:ExternalGradleUserHome = Get-ExternalGradleUserHome
$script:ProjectGradleCache = Get-GravesownPath (Join-Path 'cache\projects' (Get-ProjectCacheKey))
$env:GRAVESOWN_HOME = $script:GravesownHome
$env:GRADLE_USER_HOME = $script:ExternalGradleUserHome

function Enter-ProjectRoot {
    Set-Location -LiteralPath $script:ProjectRoot
}

function Write-Step {
    param([Parameter(Mandatory = $true)][string]$Message)
    Write-Host ''
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Get-JavaVersionText {
    param([Parameter(Mandatory = $true)][string]$JavaExecutable)

    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $JavaExecutable
    $startInfo.Arguments = '-version'
    $startInfo.UseShellExecute = $false
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $startInfo.CreateNoWindow = $true

    $process = [System.Diagnostics.Process]::Start($startInfo)
    $standardOutput = $process.StandardOutput.ReadToEnd()
    $standardError = $process.StandardError.ReadToEnd()
    $process.WaitForExit()
    $output = "$standardOutput$standardError"

    if ($process.ExitCode -ne 0) {
        throw "Java failed to start: $output"
    }

    return $output.Trim()
}

function Get-JavaMajorVersion {
    param([Parameter(Mandatory = $true)][string]$JavaExecutable)

    $output = Get-JavaVersionText -JavaExecutable $JavaExecutable
    if ($output -notmatch 'version "(?<major>[0-9]+)(?:\.|\")') {
        throw "Cannot parse Java version from: $output"
    }

    return [int]$Matches.major
}

function Use-ProjectJava {
    $portableHome = Get-GravesownPath 'runtime\jdk-21'
    $portableJava = Join-Path $portableHome 'bin\java.exe'
    $legacyHome = Join-Path $script:ProjectRoot '.tools\jdk-21'
    $legacyJava = Join-Path $legacyHome 'bin\java.exe'

    if (Test-Path -LiteralPath $portableJava) {
        $javaHome = $portableHome
        $javaExecutable = $portableJava
    }
    elseif (Test-Path -LiteralPath $legacyJava) {
        # Temporary compatibility for a workspace created before external
        # runtime storage. setup.cmd migrates this directory automatically.
        $javaHome = $legacyHome
        $javaExecutable = $legacyJava
    }
    else {
        $javaCommand = Get-Command java -ErrorAction SilentlyContinue
        if (-not $javaCommand) {
            throw 'Java 21 is not available. Run setup.cmd first.'
        }

        $javaExecutable = $javaCommand.Source
        $javaHome = Split-Path -Parent (Split-Path -Parent $javaExecutable)
    }

    $major = Get-JavaMajorVersion -JavaExecutable $javaExecutable
    if ($major -ne 21) {
        throw "Java 21 is required, but Java $major was found at $javaExecutable. Run setup.cmd."
    }

    $env:JAVA_HOME = $javaHome
    $env:Path = "$(Join-Path $javaHome 'bin');$env:Path"
    return $javaHome
}

function Invoke-ProjectGradle {
    param(
        [Parameter(Mandatory = $true, ValueFromRemainingArguments = $true)]
        [string[]]$GradleArguments
    )

    Enter-ProjectRoot
    $wrapper = Join-Path $script:ProjectRoot 'gradlew.bat'
    if (-not (Test-Path -LiteralPath $wrapper)) {
        throw "Gradle Wrapper is missing: $wrapper"
    }

    New-Item -ItemType Directory -Force -Path $script:ExternalGradleUserHome | Out-Null
    New-Item -ItemType Directory -Force -Path $script:ProjectGradleCache | Out-Null

    $externalArguments = @(
        '--gradle-user-home', $script:ExternalGradleUserHome,
        '--project-cache-dir', $script:ProjectGradleCache
    )
    & $wrapper @externalArguments @GradleArguments
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle failed with exit code ${LASTEXITCODE}: $($GradleArguments -join ' ')"
    }
}

function Get-ReleaseJar {
    $libs = Join-Path $script:ProjectRoot 'build\libs'
    if (-not (Test-Path -LiteralPath $libs)) {
        throw 'build/libs does not exist. Run build first.'
    }

    $jars = Get-ChildItem -LiteralPath $libs -File -Filter '*.jar' |
        Where-Object { $_.Name -notmatch '(-sources|-javadoc|-dev|-all)\.jar$' } |
        Sort-Object LastWriteTime -Descending

    if (-not $jars) {
        throw 'No release JAR found in build/libs.'
    }

    return $jars[0]
}
