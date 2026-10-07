@echo off
rem ============================================================
rem  Start Tomcat 9 with JPDA remote debugging enabled (port 8000)
rem  Then attach with the VSCode launch configuration
rem  "远程调试 Tomcat（需用 start-tomcat-debug.cmd 启动）".
rem  Note: keep this file ASCII-only, cmd.exe reads .cmd as GBK.
rem ============================================================
setlocal
set "JAVA_HOME=D:\java\jdk1.8.0_181"
set "CATALINA_HOME=D:\tomcat9\apache-tomcat-9.0.118"

set "CATALINA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
call "%CATALINA_HOME%\bin\catalina.bat" jpda run
endlocal
