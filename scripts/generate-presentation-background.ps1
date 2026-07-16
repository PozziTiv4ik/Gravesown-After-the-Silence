[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. "$PSScriptRoot\common.ps1"
Enter-ProjectRoot
Add-Type -AssemblyName System.Drawing

# launcher_background_source.png is the immutable composition master.  The
# generated launcher_background.png is never used as an input, so consecutive
# art/build runs cannot compound the colour grade.
$sourcePath = Join-Path $script:ProjectRoot 'launcher\assets\launcher_background_source.png'
$outputPath = Join-Path $script:ProjectRoot 'launcher\assets\launcher_background.png'

if (-not (Test-Path -LiteralPath $sourcePath -PathType Leaf)) {
    throw "Presentation background source is missing: $sourcePath"
}

if (-not ('GravesownPresentationGrade' -as [type])) {
    Add-Type -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.IO;
using System.Runtime.InteropServices;

public static class GravesownPresentationGrade
{
    private static readonly int[] InputStops = { 0, 26, 58, 102, 158, 238 };
    private static readonly Color[] ColdRamp = {
        Color.FromArgb(0x07, 0x14, 0x23),
        Color.FromArgb(0x10, 0x24, 0x3B),
        Color.FromArgb(0x29, 0x47, 0x64),
        Color.FromArgb(0x3F, 0x63, 0x85),
        Color.FromArgb(0x8F, 0xAF, 0xC8),
        Color.FromArgb(0xD7, 0xE6, 0xF2)
    };
    private static readonly Color Cyan = Color.FromArgb(0x4C, 0xA8, 0xE8);

    public static void Apply(string sourcePath, string outputPath)
    {
        using (var decoded = new Bitmap(sourcePath))
        {
            if (decoded.Width != 1672 || decoded.Height != 941)
                throw new InvalidOperationException(
                    "Expected a 1672x941 presentation master, got " + decoded.Width + "x" + decoded.Height);

            using (var source = new Bitmap(decoded.Width, decoded.Height, PixelFormat.Format32bppArgb))
            using (var output = new Bitmap(decoded.Width, decoded.Height, PixelFormat.Format32bppArgb))
            {
                using (Graphics graphics = Graphics.FromImage(source))
                {
                    graphics.CompositingMode = CompositingMode.SourceCopy;
                    graphics.DrawImageUnscaled(decoded, 0, 0);
                }

                Rectangle bounds = new Rectangle(0, 0, source.Width, source.Height);
                BitmapData sourceData = source.LockBits(bounds, ImageLockMode.ReadOnly, PixelFormat.Format32bppArgb);
                BitmapData outputData = output.LockBits(bounds, ImageLockMode.WriteOnly, PixelFormat.Format32bppArgb);
                try
                {
                    int sourceStride = Math.Abs(sourceData.Stride);
                    int outputStride = Math.Abs(outputData.Stride);
                    byte[] sourceBytes = new byte[sourceStride * source.Height];
                    byte[] outputBytes = new byte[outputStride * output.Height];
                    Marshal.Copy(sourceData.Scan0, sourceBytes, 0, sourceBytes.Length);

                    for (int y = 0; y < source.Height; y++)
                    {
                        int sourceRow = sourceData.Stride >= 0 ? y * sourceStride : (source.Height - 1 - y) * sourceStride;
                        int outputRow = outputData.Stride >= 0 ? y * outputStride : (output.Height - 1 - y) * outputStride;
                        for (int x = 0; x < source.Width; x++)
                        {
                            int sourceIndex = sourceRow + x * 4;
                            int outputIndex = outputRow + x * 4;
                            byte blue = sourceBytes[sourceIndex];
                            byte green = sourceBytes[sourceIndex + 1];
                            byte red = sourceBytes[sourceIndex + 2];
                            byte alpha = sourceBytes[sourceIndex + 3];

                            int luminance = (54 * red + 183 * green + 19 * blue + 128) >> 8;
                            Color graded = SampleColdRamp(luminance);

                            // The source illustration already contains sparse red
                            // ember detail.  Translating only those source-authored
                            // hot pixels into restrained cyan preserves the scene's
                            // focal sparks without introducing procedural noise.
                            int warmDominance = red - Math.Max(green, blue);
                            if (red > 45 && warmDominance > 12)
                            {
                                double cyanMix = Math.Min(0.64, (warmDominance - 12) / 112.0 * 0.64);
                                graded = Blend(graded, Cyan, cyanMix);
                            }

                            outputBytes[outputIndex] = graded.B;
                            outputBytes[outputIndex + 1] = graded.G;
                            outputBytes[outputIndex + 2] = graded.R;
                            outputBytes[outputIndex + 3] = alpha;
                        }
                    }

                    Marshal.Copy(outputBytes, 0, outputData.Scan0, outputBytes.Length);
                }
                finally
                {
                    source.UnlockBits(sourceData);
                    output.UnlockBits(outputData);
                }

                string parent = Path.GetDirectoryName(outputPath);
                if (!String.IsNullOrEmpty(parent)) Directory.CreateDirectory(parent);
                string temporary = outputPath + ".tmp.png";
                output.Save(temporary, ImageFormat.Png);
                File.Copy(temporary, outputPath, true);
                File.Delete(temporary);
            }
        }
    }

    private static Color SampleColdRamp(int luminance)
    {
        if (luminance <= InputStops[0]) return ColdRamp[0];
        for (int index = 1; index < InputStops.Length; index++)
        {
            if (luminance <= InputStops[index])
            {
                double amount = (luminance - InputStops[index - 1]) /
                    (double)(InputStops[index] - InputStops[index - 1]);
                return Blend(ColdRamp[index - 1], ColdRamp[index], amount);
            }
        }
        return ColdRamp[ColdRamp.Length - 1];
    }

    private static Color Blend(Color from, Color to, double amount)
    {
        double clamped = Math.Max(0.0, Math.Min(1.0, amount));
        int red = (int)Math.Round(from.R + (to.R - from.R) * clamped);
        int green = (int)Math.Round(from.G + (to.G - from.G) * clamped);
        int blue = (int)Math.Round(from.B + (to.B - from.B) * clamped);
        return Color.FromArgb(red, green, blue);
    }
}
'@ -ReferencedAssemblies System.Drawing
}

[GravesownPresentationGrade]::Apply($sourcePath, $outputPath)
$hash = (Get-FileHash -LiteralPath $outputPath -Algorithm SHA256).Hash
Write-Host "PASS Cold navy presentation background: $outputPath" -ForegroundColor Green
Write-Host "Source: $sourcePath"
Write-Host "SHA-256: $hash"
