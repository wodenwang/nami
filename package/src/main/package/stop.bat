@echo off
title NAMI Server Management
color 0a

set PWD=%~dp0
set INSTALLATION_DIR=%PWD%

echo *********************************************************
echo ****************  NAMI Server Management  ***************
echo ****************  Born for Wechat Applet  ***************
echo *********************************************************
echo.

set CATALINA_HOME=%INSTALLATION_DIR%appserver\tomcat

set CATALINA_BASE=%INSTALLATION_DIR%appserver\CATALINA_BASE

echo JAVA_HOME:		%JAVA_HOME%
echo CATALINA_BASE: %CATALINA_BASE%
echo CATALINA_HOME: %CATALINA_HOME%

call %CATALINA_HOME%\bin\catalina.bat stop

pause