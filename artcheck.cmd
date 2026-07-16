@echo off
setlocal
powershell -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\verify-art.ps1" -WriteContactSheets
exit /b %errorlevel%
