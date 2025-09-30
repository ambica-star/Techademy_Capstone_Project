@echo off
setlocal

REM ========================================
REM NSE Stock Testing Framework
REM Cross-Browser Automation Testing
REM ========================================

REM Set JAVA_HOME to JDK 1.8.0_91
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_91"

REM Set Maven path
set "MAVEN_HOME=%CD%\tools\apache-maven-3.9.5"

REM Update PATH
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

REM Display header
echo ========================================
echo NSE Stock Testing Framework
echo Cross-Browser Automation Testing
echo ========================================
echo.

REM Verify Java version
echo Checking Java version...
java -version
echo.

REM Run tests
echo ========================================
echo Running Cross-Browser Tests...
echo Testing on: Chrome, Firefox, Edge
echo ========================================
echo.
mvn clean test

REM Check if tests completed
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Tests Completed Successfully!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo Tests Completed with Errors
    echo ========================================
)

REM Open the report
echo.
echo Opening Test Report...
if exist "ExtentReport.html" (
    start ExtentReport.html
    echo Report opened successfully!
) else (
    echo Warning: Report file not found!
)

echo.
echo ========================================
echo Test Execution Complete
echo ========================================
echo.

endlocal
pause

