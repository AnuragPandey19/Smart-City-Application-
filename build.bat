@echo off
REM ===================================================================
REM   Smart City - build a self-contained runnable JAR.
REM
REM   Output: dist\SmartCity.jar  (double-clickable; needs Java 11+)
REM
REM   Requirements: a JDK on PATH (javac + jar commands available).
REM ===================================================================

setlocal enableextensions
cd /d "%~dp0"

set "MAIN=com.smartcity.MainFrame"
set "MYSQL_JAR=lib\mysql-connector-j-9.2.0.jar"
set "OUT_JAR=dist\SmartCity.jar"
set "BUILD=build"

REM --- Sanity checks -----------------------------------------------
where javac >nul 2>nul || (
    echo [ERROR] javac not found on PATH. Install a JDK 11+ and try again.
    exit /b 1
)
where jar >nul 2>nul || (
    echo [ERROR] jar not found on PATH. Install a JDK 11+ and try again.
    exit /b 1
)
if not exist "%MYSQL_JAR%" (
    echo [ERROR] %MYSQL_JAR% missing. Place the MySQL connector jar in lib\.
    exit /b 1
)

REM --- Clean previous output ---------------------------------------
if exist "%BUILD%" rmdir /s /q "%BUILD%"
if exist dist     rmdir /s /q dist
mkdir "%BUILD%"
mkdir dist

REM --- 1. Compile --------------------------------------------------
REM   Pass the wildcard directly so javac does its own filename
REM   expansion. That avoids any argfile quoting issues with paths
REM   that contain spaces (e.g. "Smart City").
echo [1/4] Compiling Java sources...
javac -encoding UTF-8 -d "%BUILD%" -cp "%MYSQL_JAR%" src\com\smartcity\*.java
if not "%ERRORLEVEL%"=="0" (
    echo [ERROR] Compilation failed.
    exit /b 1
)

REM --- 2. Unpack MySQL driver into build dir -----------------------
echo [2/4] Bundling MySQL driver classes...
pushd "%BUILD%"
jar xf "..\%MYSQL_JAR%"
REM strip signature files (we'll rewrite the manifest below)
if exist META-INF\*.RSA del /q META-INF\*.RSA
if exist META-INF\*.SF  del /q META-INF\*.SF
if exist META-INF\*.DSA del /q META-INF\*.DSA
if exist META-INF\MANIFEST.MF del /q META-INF\MANIFEST.MF
popd

REM --- 3. Bundle schema.sql as classpath resource ------------------
echo [3/4] Bundling schema.sql...
mkdir "%BUILD%\resources" 2>nul
copy /Y "src\resources\schema.sql" "%BUILD%\resources\schema.sql" >nul

REM --- 4. Assemble the JAR -----------------------------------------
echo [4/4] Packaging %OUT_JAR%...
> "%BUILD%\manifest.mf" echo Manifest-Version: 1.0
>> "%BUILD%\manifest.mf" echo Main-Class: %MAIN%
>> "%BUILD%\manifest.mf" echo.

pushd "%BUILD%"
REM Include com (our classes), resources (schema.sql), META-INF (driver service),
REM google + org + etc. (whatever the MySQL jar shipped). Use "." for completeness.
jar cfm "..\%OUT_JAR%" manifest.mf .
popd

echo.
echo ================================================================
echo  BUILD COMPLETE
echo ================================================================
echo  Output:  %OUT_JAR%
echo  Run it:  java -jar "%OUT_JAR%"
echo  (or just double-click the file in Explorer)
echo.
echo  Next step: run package.bat to wrap this into a Windows installer
echo  with a bundled Java runtime (no Java needed on user machines).
echo ================================================================
endlocal
