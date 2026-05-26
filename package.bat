@echo off
REM ===================================================================
REM   Smart City - wrap the runnable JAR into a Windows app-image
REM   using jpackage. Bundles its own JRE — end-users don't need Java.
REM
REM   Output: installer\Smart City\Smart City.exe
REM
REM   Requirements: JDK 17+ (jpackage ships with it).
REM ===================================================================

setlocal enableextensions
cd /d "%~dp0"

set "JAR=dist\SmartCity.jar"
set "OUT_DIR=installer"

if not exist "%JAR%" (
    echo [ERROR] %JAR% not found. Run build.bat first.
    exit /b 1
)

where jpackage >nul 2>nul || (
    echo [ERROR] jpackage not on PATH.
    echo         jpackage ships with JDK 17 and newer. Install one and try again.
    exit /b 1
)

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

echo Packaging "Smart City" as a Windows app-image...
echo (this takes ~30 seconds — jpackage is copying a JRE into the folder)
echo.

jpackage ^
    --type app-image ^
    --name "Smart City" ^
    --input dist ^
    --main-jar SmartCity.jar ^
    --main-class com.smartcity.MainFrame ^
    --dest "%OUT_DIR%" ^
    --vendor "Smart City" ^
    --app-version "1.0" ^
    --description "Smart City Guide — city info, tourism, transport, shopping"

if errorlevel 1 (
    echo [ERROR] jpackage failed.
    exit /b 1
)

echo.
echo ================================================================
echo  PACKAGE COMPLETE
echo ================================================================
echo  Folder: %OUT_DIR%\Smart City\
echo  Launch: double-click "%OUT_DIR%\Smart City\Smart City.exe"
echo.
echo  To distribute: zip the whole "Smart City" folder and share it.
echo  The recipient just unzips and double-clicks the .exe inside.
echo.
echo  Want a proper Windows installer (.exe with Start Menu shortcut)?
echo  Install Inno Setup, then replace --type app-image with --type exe.
echo  Want an MSI?  Install WiX Toolset, then use --type msi.
echo ================================================================
endlocal
