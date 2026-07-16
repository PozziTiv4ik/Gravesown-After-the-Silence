@echo off
setlocal
cd /d "%~dp0"
powershell -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0build-launcher.ps1"
set "EXIT_CODE=%ERRORLEVEL%"
if not "%EXIT_CODE%"=="0" pause
exit /b %EXIT_CODE%
