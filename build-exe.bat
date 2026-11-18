@echo off
setlocal

cd /d "%~dp0"

set "JSMOOTH=C:\Program Files\JSmooth 0.9.9-7\jsmoothcmd.exe"
if not exist "%JSMOOTH%" set "JSMOOTH=C:\Program Files (x86)\JSmooth 0.9.9-7\jsmoothcmd.exe"

if not exist "%JSMOOTH%" (
    echo JSmooth not found. Install it or set JSMOOTH to jsmoothcmd.exe
    exit /b 1
)

echo Building shaded JAR...
call gradlew.bat shadowJar
if errorlevel 1 (
    echo Gradle build failed.
    exit /b 1
)

echo Packaging EXE with JSmooth...
"%JSMOOTH%" ".github\SheetsIO-exe-packager.jsmooth"
if errorlevel 1 (
    echo JSmooth packaging failed.
    exit /b 1
)

echo Done: build\libs\SheetsIO.exe
pause
