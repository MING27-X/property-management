@echo off
rem ============================================================
rem  Initialize the MySQL database property_db
rem  (drops the old database and recreates it from db\property_db.sql)
rem  Note: keep this file ASCII-only, cmd.exe reads .cmd as GBK.
rem ============================================================
setlocal
set "MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set "PROJECT_DIR=%~dp0.."
set "SQL_FILE=%PROJECT_DIR%\db\property_db.sql"

if not exist "%MYSQL_BIN%" (
    echo [ERROR] mysql client not found: %MYSQL_BIN%
    exit /b 1
)

if not exist "%SQL_FILE%" (
    echo [ERROR] SQL file not found: %SQL_FILE%
    exit /b 1
)

echo [INFO] Running %SQL_FILE% ...
rem Note: the mysql client cannot open a path argument containing non-ASCII
rem characters, so the SQL file is fed through stdin redirection instead.
"%MYSQL_BIN%" -uroot -p123456 --default-character-set=utf8mb4 < "%SQL_FILE%"
if errorlevel 1 (
    echo [ERROR] Database initialization failed.
    exit /b 1
)

echo [INFO] Database initialized.
endlocal
