@echo off
echo ========================================
echo  NSE Stock Testing Framework
echo  Chrome Browser Tests Only
echo ========================================

REM Set environment variables
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_91"
set "MAVEN_HOME=%CD%\tools\apache-maven-3.9.5"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo Environment Configuration:
echo JAVA_HOME: %JAVA_HOME%
echo MAVEN_HOME: %MAVEN_HOME%
echo.

echo Verifying Java installation...
java -version
echo.

echo Verifying Maven installation...
mvn -version
echo.

echo Cleaning previous test results...
if exist "target" rmdir /s /q target
if exist "test-output" rmdir /s /q test-output

echo Killing any existing browser processes...
taskkill /F /IM chrome.exe /T >nul 2>&1
taskkill /F /IM chromedriver.exe /T >nul 2>&1

echo.
echo Starting Chrome tests...
echo This will download the latest ChromeDriver compatible with your Chrome version
echo ========================================
"%MAVEN_HOME%\bin\mvn.cmd" clean test -Dbrowser=chrome

echo.
echo Cleaning up browser processes...
taskkill /F /IM chrome.exe /T >nul 2>&1
taskkill /F /IM chromedriver.exe /T >nul 2>&1

echo.
echo ========================================
echo Test execution completed!
echo.
echo Generated Reports:
echo - ExtentReports: test-output\reports\ExtentReport.html
echo - TestNG Reports: target\surefire-reports\emailable-report.html
echo - Screenshots: test-output\screenshots\
echo - Logs: test-output\logs\automation.log
echo ========================================

if exist "target\surefire-reports\emailable-report.html" (
    start "" "target\surefire-reports\emailable-report.html"
)

pause
