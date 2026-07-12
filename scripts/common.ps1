Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$script:ProjectRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path

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
    $portableHome = Join-Path $script:ProjectRoot '.tools\jdk-21'
    $portableJava = Join-Path $portableHome 'bin\java.exe'

    if (Test-Path -LiteralPath $portableJava) {
        $javaHome = $portableHome
        $javaExecutable = $portableJava
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

    & $wrapper @GradleArguments
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
