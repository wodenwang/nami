@ECHO OFF
:: Check Windows version
IF NOT "%OS%"=="Windows_NT" GOTO Syntax
:: Check command line
ECHO.%1 | FIND "?" >NUL
IF NOT ERRORLEVEL 1 GOTO Syntax
IF [%3]==[] GOTO Syntax
:: Check if INI file exists
IF NOT EXIST "%~f1" GOTO Syntax

:: Keep variables local and enable delayed variable expansion
SETLOCAL ENABLEDELAYEDEXPANSION

:: Read variables from command line
SET INIFile="%~f1"
SET INISection=%~2
SET INIKey=%~3
SET INIValue=

:: Reset temporary variables
SET SectOK=0
SET SectFound=0
SET KeyFound=0

:: Search the INI file line by line
FOR /F "tokens=* delims=" %%A IN ('TYPE %INIFile%') DO CALL :ParseINI "%%A"

:: Display the result
ECHO.
:: EXIT /B return codes (errorlevels) added by Van Woods,
:: US Army Corps of Engineers, Seattle District
IF NOT %SectFound%==1 (
    ECHO INI section not found
    EXIT /B 1
) ELSE (
    IF NOT %KeyFound%==1 (
        EXIT /B 2
    ) ELSE (
        IF DEFINED INIValue (
            ECHO.%INIFile%
            ECHO [%INISection%]
            ECHO %INIKey%=%INIValue%
        ) ELSE (
            ECHO Value not defined
            EXIT /B 3
        )
    )
)

:: Corrected environment variable by Van Woods,
:: US Army Corps of Engineers, Seattle District
ENDLOCAL & SET %INIKey%=%INIValue%
GOTO:EOF


:ParseINI
:: Skip rest of file after key has been found;
:: speed improvement by Jeroen Verschuuren
IF "%SectFound%"=="1" IF "%KeyFound%"=="1" GOTO:EOF
:: Store quoted line in variable
SET Line="%~1"

:: Check if this line is the required section heading
ECHO.%Line%| FIND /I "[%INISection%]" >NUL
IF NOT ERRORLEVEL 1 (
    SET SectOK=1
    SET SectFound=1
    GOTO:EOF
)
:: Check if this line is a different section header
IF "%Line:~1,1%"=="[" SET SectOK=0
IF %SectOK%==0 GOTO:EOF

:: Parse any "key=value" line
FOR /F "tokens=1* delims==" %%a IN ('ECHO.%Line%') DO (
    SET Key=%%a^"
    SET Value=^"%%b
)

:: Strip quotes, tabs, and surrounding spaces from key and value
:: Modifications added by Van Woods,
:: US Army Corps of Engineers, Seattle District
SET Value=%Value:"=%
:: Remove quotes
SET Key=%Key:"=%
:: Remove tabs
SET Value=%Value:	=%
SET Key=%Key:	=%
:: Remove leading spaces
FOR /F "tokens=* delims= " %%A IN ("%Key%")   DO SET Key=%%A
FOR /F "tokens=* delims= " %%A IN ("%Value%") DO SET Value=%%A
:: Remove trailing spaces
FOR /L %%A in (1,1,32) do if "!Key:~-1!"==" " set Key=!Key:~0,-1!
FOR /L %%A in (1,1,32) do if "!Value:~-1!"==" " set Value=!Value:~0,-1!

:: Now check if the key matches the required key
IF /I "%Key%"=="%INIKey%" (
    SET INIValue=%Value%
    SET KeyFound=1
)

:: End of ParseINI subroutine
GOTO:EOF



:Syntax
ECHO.
ECHO ReadINI.bat,  Version 1.30 for Windows NT 4 / 2000 / XP / Server 2003
ECHO Read a value from the specified INI file
ECHO.
ECHO Usage:  READINI  "ini_file"  "section"  "key"
ECHO Where:           "ini_file" is the file name of the INI file to be read
ECHO                  "section"  is the section name, without the brackets
ECHO                  "key"      is the key whose value must be read
ECHO.
ECHO Example: if MYPROG.INI looks like this:
ECHO     [Section 1]
ECHO     Key1=Value 1
ECHO     Key2=Value 2
ECHO     [Section 2]
ECHO     Key2=Value 4
ECHO Then the command:  READINI "MYPROG.INI" "section 1" "key2"
ECHO will return:       Key2=Value 2
ECHO.
ECHO Written by Rob van der Woude
ECHO http://www.robvanderwoude.com
ECHO Speed improvement by Jeroen Verschuuren
ECHO Return codes, whitespace removal and corrected environment variable
ECHO by Van Woods, US Army Corps of Engineers, Seattle District