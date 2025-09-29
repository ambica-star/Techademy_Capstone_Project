@echo off
echo ========================================
echo  NSE STOCK TESTING FRAMEWORK
echo  EDGE BROWSER EXECUTION
echo  CAPSTONE PROJECT
echo ========================================

REM Set environment variables
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_91"
set "MAVEN_HOME=%CD%\tools\apache-maven-3.9.5"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo.
echo Environment Setup:
echo JAVA_HOME: %JAVA_HOME%
echo MAVEN_HOME: %MAVEN_HOME%
echo Browser: Edge
echo.

echo Verifying Java version...
java -version
echo.

echo Cleaning previous results and creating fresh report directories...
if exist "target" rmdir /s /q target
if exist "src\reports" rmdir /s /q src\reports
mkdir src\reports\screenshots
mkdir src\reports\html
mkdir src\reports\logs

echo.
echo Killing any existing Edge processes...
taskkill /F /IM msedge.exe /T >nul 2>&1
taskkill /F /IM msedgedriver.exe /T >nul 2>&1

echo.
echo ========================================
echo  EXECUTING EDGE BROWSER TESTS
echo ========================================
echo.

echo Starting Edge browser tests with WebDriverManager...
"%MAVEN_HOME%\bin\mvn.cmd" clean test -Dbrowser=edge -DsuiteXmlFile=src/test/resources/testng-single-browser.xml

echo.
echo ========================================
echo  EDGE EXECUTION COMPLETED
echo ========================================

echo.
echo Final cleanup of Edge processes...
taskkill /F /IM msedge.exe /T >nul 2>&1
taskkill /F /IM msedgedriver.exe /T >nul 2>&1

echo.
echo ========================================
echo  EDGE EXECUTION ARTIFACTS
echo ========================================
echo.

echo Screenshots (src/reports/screenshots):
if exist "src\reports\screenshots" (
    dir src\reports\screenshots
) else (
    echo No screenshots directory found
)

echo.
echo HTML Reports (src/reports/html):
if exist "src\reports\html" (
    dir src\reports\html
) else (
    echo No HTML reports directory found
)

echo.
echo TestNG Reports (target/surefire-reports):
if exist "target\surefire-reports" (
    dir target\surefire-reports\*.html
) else (
    echo No TestNG reports directory found
)

echo.
echo ========================================
echo  EDGE EXECUTION SUMMARY
echo ========================================
echo [✓] Browser: Edge - Microsoft EdgeDriver support
echo [✓] Framework: Java 8 + Selenium 3.x + WebDriverManager 5.6.2
echo [✓] NSE Website: https://www.nseindia.com/ automated testing
echo [✓] Stock Testing: NIFTY 50 stocks with real-time data
echo [✓] Screenshots: High-quality execution evidence
echo [✓] Reports: ExtentReports + TestNG HTML reports
echo.
echo Edge execution completed successfully!

pause
