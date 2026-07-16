@echo off
setlocal
powershell -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\gradle-offline.ps1" %*
exit /b %errorlevel%
