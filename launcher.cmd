@echo off
setlocal
cd /d "%~dp0"
set "LAUNCHER_EXE=%~dp0launcher\dist\Gravesown Launcher\Gravesown Launcher.exe"
if not exist "%LAUNCHER_EXE%" (
    echo Gravesown Launcher is being prepared for first use...
    call "%~dp0setup.cmd"
    if errorlevel 1 exit /b 1
    call "%~dp0launcher\build-launcher.cmd"
    if errorlevel 1 exit /b 1
)
start "" "%LAUNCHER_EXE%"
exit /b 0
