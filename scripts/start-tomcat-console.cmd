@echo off
rem Start Tomcat 9 in the foreground (console visible, close it to stop)
setlocal
set "JAVA_HOME=D:\java\jdk1.8.0_181"
set "CATALINA_HOME=D:\tomcat9\apache-tomcat-9.0.118"

call "%CATALINA_HOME%\bin\catalina.bat" run
endlocal
