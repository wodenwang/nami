@echo off

title NAMI Tools 服务器管理工具
color 0a

set PWD=%~dp0
set INSTALLATION_DIR=%PWD%

echo *********************************************************
echo ******************** NAMI Tools (C)(R) ******************
echo **********************  服务器管理工具  *******************
echo *********************************************************
echo.

set CATALINA_HOME=%INSTALLATION_DIR%\appserver\tomcat

set CATALINA_BASE=%INSTALLATION_DIR%\appserver\tomcat\CATALINA_BASE

echo CATALINA_BASE: %CATALINA_BASE%
echo CATALINA_HOME: %CATALINA_HOME%

call %CATALINA_HOME%\bin\catalina.bat stop

pause