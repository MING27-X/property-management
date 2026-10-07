@echo off
rem ============================================================
rem  Deploy the WAR to the local Tomcat 9 and restart Tomcat
rem  Usage: scripts\deploy.cmd
rem  Note: keep this file ASCII-only, cmd.exe reads .cmd as GBK.
rem ============================================================
setlocal

set "TOMCAT_HOME=D:\tomcat9\apache-tomcat-9.0.118"
set "PROJECT_DIR=%~dp0.."
set "WAR=%PROJECT_DIR%\target\property-ms.war"

if not exist "%WAR%" (
    echo [ERROR] %WAR% not found, run scripts\build.cmd -o clean package first
    exit /b 1
)

echo [INFO] Stopping Tomcat ...
call "%~dp0stop-tomcat.cmd"

echo [INFO] Copying WAR to %TOMCAT_HOME%\webapps ...
copy /Y "%WAR%" "%TOMCAT_HOME%\webapps\property-ms.war" >nul

if exist "%TOMCAT_HOME%\webapps\property-ms" rd /s /q "%TOMCAT_HOME%\webapps\property-ms"

echo [INFO] Starting Tomcat ...
call "%~dp0start-tomcat.cmd"

echo [INFO] Deployed. Open: http://localhost:8888/property-ms/
endlocal
