@echo off

set THISDIR=%~dp0

:: Use cutomer's choose JDK if set, can use tools\JDK.bat to set
call %THISDIR%\readini %THISDIR%\settings.ini JDK JDK_HOME
if not "%JDK_HOME%" == "" (
	set JAVA_HOME=%JDK_HOME%
)

:: To use build-in JDK if customer didn't set in settings.ini or env.
if "%JAVA_HOME%" == "" goto USINGBUILTIN

:: If JAVA_HOME exists, check the version
for %%x in ("%JAVA_HOME%") do set JAVA_HOME=%%~sx
set EXE_JAVA=%JAVA_HOME%\bin\java

set ReqVer=1.8
for /F "tokens=1,2 delims=." %%A in ("%ReqVer%") do (
	set ReqMajorVer=%%A
	set ReqMinorVer=%%B
)
for /F "tokens=3" %%A in ('%EXE_JAVA% -version 2^>^&1 ^| find /I "java version"') do (
	for /F "tokens=1,2 delims=." %%B in ("%%~A") do (
		set JavaMajorVer=%%B
		set JavaMinorVer=%%C
	)
)
IF %ReqMajorVer% GTR %JavaMajorVer% (
	goto USINGBUILTIN
) ELSE (
	IF %ReqMinorVer% GTR %JavaMinorVer% (
		goto USINGBUILTIN
	) ELSE (
		goto FINI
	)
)

:USINGBUILTIN
if exist %THISDIR%\win32 set JAVA_HOME=%THISDIR%\win32
if exist %THISDIR%\win64 set JAVA_HOME=%THISDIR%\win64

:FINI


