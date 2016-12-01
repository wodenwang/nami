@echo off
title NAMI Server Management
color 0a

set PWD=%~dp0
set INSTALLATION_DIR=%PWD%

echo *********************************************************
echo ****************  NAMI Server Management  ***************
echo ****************       H2 Db Tools        ***************
echo *********************************************************
echo.
	
echo JAVA_HOME:		%JAVA_HOME%

@java -cp "%INSTALLATION_DIR%nami\WEB-INF\lib\h2.jar;%CLASSPATH%" org.h2.tools.Console -url jdbc:h2:./database/nami;AUTO_SERVER=TRUE;MVCC=TRUE -driver org.h2.Driver -user sa %*
@if errorlevel 1 pause
