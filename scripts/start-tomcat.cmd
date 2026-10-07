@echo off
rem ============================================================
rem  Start Tomcat 9 in the background (hidden window,
rem  console output goes to Tomcat\logs\catalina-console.log)
rem  Use scripts\start-tomcat-console.cmd to watch the console.
rem  Note: keep this file ASCII-only, cmd.exe reads .cmd as GBK.
rem ============================================================
setlocal
set "JAVA_HOME=D:\java\jdk1.8.0_181"
set "CATALINA_HOME=D:\tomcat9\apache-tomcat-9.0.118"
set "LOG_DIR=%CATALINA_HOME%\logs"

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

powershell -NoProfile -ExecutionPolicy Bypass -Command "Start-Process -FilePath '%CATALINA_HOME%\bin\catalina.bat' -ArgumentList 'run' -WindowStyle Hidden -RedirectStandardOutput '%LOG_DIR%\catalina-console.log' -RedirectStandardError '%LOG_DIR%\catalina-console.err.log'"

rem wait for Tomcat to start (timeout.exe fails when stdin is redirected)
powershell -NoProfile -Command "Start-Sleep -Seconds 15"
endlocal
