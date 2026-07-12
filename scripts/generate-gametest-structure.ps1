Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$projectRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$outputPath = Join-Path $projectRoot 'src\main\resources\data\gravesown\structure\hollow_grazer_platform.nbt'
$outputDirectory = Split-Path -Parent $outputPath
New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null

$file = [System.IO.File]::Create($outputPath)
$gzip = [System.IO.Compression.GZipStream]::new(
    $file,
    [System.IO.Compression.CompressionLevel]::Optimal
)
$writer = [System.IO.BinaryWriter]::new($gzip, [System.Text.Encoding]::UTF8, $false)

function Write-Int16BigEndian {
    param([Parameter(Mandatory = $true)][int]$Value)
    $bytes = [System.BitConverter]::GetBytes([int16]$Value)
    if ([System.BitConverter]::IsLittleEndian) {
        [System.Array]::Reverse($bytes)
    }
    $writer.Write($bytes)
}

function Write-Int32BigEndian {
    param([Parameter(Mandatory = $true)][int]$Value)
    $bytes = [System.BitConverter]::GetBytes([int32]$Value)
    if ([System.BitConverter]::IsLittleEndian) {
        [System.Array]::Reverse($bytes)
    }
    $writer.Write($bytes)
}

function Write-NbtString {
    param([Parameter(Mandatory = $true)][AllowEmptyString()][string]$Value)
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($Value)
    Write-Int16BigEndian -Value $bytes.Length
    $writer.Write($bytes)
}

function Write-NamedTagHeader {
    param(
        [Parameter(Mandatory = $true)][byte]$Type,
        [Parameter(Mandatory = $true)][string]$Name
    )
    $writer.Write($Type)
    Write-NbtString -Value $Name
}

function Write-IntListPayload {
    param([Parameter(Mandatory = $true)][int[]]$Values)
    $writer.Write([byte]3)
    Write-Int32BigEndian -Value $Values.Length
    foreach ($value in $Values) {
        Write-Int32BigEndian -Value $value
    }
}

try {
    # Root compound.
    $writer.Write([byte]10)
    Write-NbtString -Value ''

    Write-NamedTagHeader -Type 3 -Name 'DataVersion'
    Write-Int32BigEndian -Value 3955

    Write-NamedTagHeader -Type 9 -Name 'size'
    Write-IntListPayload -Values @(3, 3, 3)

    # Nine stone blocks form a 3x3 floor at y=0.
    Write-NamedTagHeader -Type 9 -Name 'blocks'
    $writer.Write([byte]10)
    Write-Int32BigEndian -Value 9
    for ($x = 0; $x -lt 3; $x++) {
        for ($z = 0; $z -lt 3; $z++) {
            Write-NamedTagHeader -Type 9 -Name 'pos'
            Write-IntListPayload -Values @($x, 0, $z)
            Write-NamedTagHeader -Type 3 -Name 'state'
            Write-Int32BigEndian -Value 0
            $writer.Write([byte]0)
        }
    }

    Write-NamedTagHeader -Type 9 -Name 'palette'
    $writer.Write([byte]10)
    Write-Int32BigEndian -Value 1
    Write-NamedTagHeader -Type 8 -Name 'Name'
    Write-NbtString -Value 'minecraft:stone'
    $writer.Write([byte]0)

    Write-NamedTagHeader -Type 9 -Name 'entities'
    $writer.Write([byte]10)
    Write-Int32BigEndian -Value 0

    $writer.Write([byte]0)
}
finally {
    $writer.Dispose()
}

Write-Host "Generated GameTest structure: $outputPath"
