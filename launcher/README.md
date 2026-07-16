# Gravesown Windows launcher

This is a local Java 21 Swing launcher for the Gravesown NeoForge client. It
does not contain or redistribute Minecraft and it does not bypass Microsoft
authentication. The **ИГРАТЬ** button starts
`scripts/bootstrap-and-play.ps1`: a fresh download runs the explicit setup
once, while prepared installations start the client fully offline.

The current layout keeps the Gravesown title centered at the top, one large animated
Play action in the center, Verify and Logs directly below, and a compact process
console at the bottom. Russian or English labels follow the Windows/JVM locale. The live window has a minimum size
of 1100x700; smaller layouts are unsupported so controls never collapse on top of
each other.

Run `build-launcher.cmd` to create the untracked app-image at:

`launcher/dist/Gravesown Launcher/Gravesown Launcher.exe`

The launcher searches its parent directories for `gradlew.bat`,
`gradle.properties` and `scripts/bootstrap-and-play.ps1`, so the packaged EXE
can be started directly from its generated location. `launcher.cmd` in the
project root runs setup when required, builds the image on first use and opens
it.

Downloaded Java, Gradle project state, client/server runs, logs and worlds live
outside the repository under `%LOCALAPPDATA%\Gravesown`. The shared dependency
cache remains under `%USERPROFILE%\.gradle`. This keeps Git clones and release
archives small and lets future launches reuse one installation.

Useful non-interactive checks after building:

```text
"launcher\dist\Gravesown Launcher\Gravesown Launcher.exe" --diagnose
"launcher\dist\Gravesown Launcher\Gravesown Launcher.exe" --render-preview preview.png 1100 700
```

`--render-preview` creates a composition PNG without showing a window or starting
Minecraft. Width/height are optional (default 1180x760). This is the preferred quick
layout check in both RU and EN environments; it does not replace a final live click
test of Play, Verify and Logs.

The original 1672x941 giant-tree composition lives in
`launcher/assets/launcher_background_source.png`. The deterministic
`scripts/generate-presentation-background.ps1` pass maps that immutable master to
the cold navy/steel/fog-blue presentation palette and writes
`launcher/assets/launcher_background.png`. Launcher builds regenerate that derived
file before bundling it; repeated builds therefore never compound the colour grade.
When the generated background is absent or unreadable, the UI uses a matching navy
gradient.

`launcher/assets/launcher_icon.png` is converted to a native ICO during the build
and is also used by the Swing window. Generated app-image/runtime files stay under
ignored `launcher/build/` and `launcher/dist/` directories.

`package-release.cmd` bundles the generated app-image with the complete source
kit and release JAR. It deliberately does not bundle Minecraft game binaries;
the first setup obtains the authorized dependencies before play.
