@echo off
rem ============================================================
rem  Smart Property Management System - Maven build script
rem  Usage: scripts\build.cmd -o clean package
rem         -o  = offline build (use jars already in the local repo)
rem  Note: keep this file ASCII-only, cmd.exe reads .cmd as GBK.
rem ============================================================
setlocal

set "JAVA_HOME=D:\java\jdk1.8.0_181"
set "MAVEN_HOME=D:\maven\apache-maven-3.9.9"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

cd /d "%~dp0.."

echo [INFO] JAVA_HOME = %JAVA_HOME%
call "%MAVEN_HOME%\bin\mvn.cmd" %*
set "RESULT=%ERRORLEVEL%"

endlocal & exit /b %RESULT%
