@ECHO OFF
SETLOCAL

set "WRAPPER_DIR=%~dp0.mvn\wrapper"
set "MAVEN_VERSION=3.9.9"
set "MAVEN_BASE=apache-maven-%MAVEN_VERSION%"
set "MAVEN_HOME=%WRAPPER_DIR%\%MAVEN_BASE%"
set "MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd"
set "SETTINGS_FILE=%~dp0.mvn\settings.xml"

if exist "%MAVEN_BIN%" goto run

if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
set "ZIP_FILE=%WRAPPER_DIR%\%MAVEN_BASE%-bin.zip"
set "DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/%MAVEN_BASE%-bin.zip"

echo [mvnw] Downloading Maven %MAVEN_VERSION%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%ZIP_FILE%'" || goto error

echo [mvnw] Extracting Maven...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%WRAPPER_DIR%' -Force" || goto error

del "%ZIP_FILE%" >nul 2>nul

:run
if exist "%SETTINGS_FILE%" (
  call "%MAVEN_BIN%" -s "%SETTINGS_FILE%" %*
) else (
  call "%MAVEN_BIN%" %*
)
exit /b %ERRORLEVEL%

:error
echo [mvnw] Failed to prepare Maven wrapper.
exit /b 1

