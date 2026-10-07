@echo off
rem Stop Tomcat 9
rem Note: keep this file ASCII-only, cmd.exe reads .cmd as GBK.
setlocal
set "JAVA_HOME=D:\java\jdk1.8.0_181"
set "CATALINA_HOME=D:\tomcat9\apache-tomcat-9.0.118"

call "%CATALINA_HOME%\bin\shutdown.bat" 2>nul
powershell -NoProfile -Command "Start-Sleep -Seconds 4"
endlocal
