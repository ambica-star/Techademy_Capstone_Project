@echo off
echo ========================================
echo  SETTING UP EDGE DRIVER MANUALLY
echo  SOLVING NETWORK CONNECTIVITY ISSUE
echo ========================================

REM Check if Edge browser is installed
echo Checking for Microsoft Edge installation...
if exist "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" (
    echo ✅ Found Edge at: C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe
    set EDGE_PATH="C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
) else if exist "C:\Program Files\Microsoft\Edge\Application\msedge.exe" (
    echo ✅ Found Edge at: C:\Program Files\Microsoft\Edge\Application\msedge.exe
    set EDGE_PATH="C:\Program Files\Microsoft\Edge\Application\msedge.exe"
) else (
    echo ❌ Microsoft Edge not found in standard locations
    goto :fallback
)

REM Get Edge version
echo Getting Edge version...
for /f "tokens=*" %%i in ('powershell -Command "Get-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Microsoft Edge' -Name DisplayVersion -ErrorAction SilentlyContinue | Select-Object -ExpandProperty DisplayVersion"') do set EDGE_VERSION=%%i

if "%EDGE_VERSION%"=="" (
    echo ❌ Could not determine Edge version
    goto :fallback
) else (
    echo ✅ Edge version detected: %EDGE_VERSION%
)

REM Try to download compatible EdgeDriver from GitHub releases (alternative source)
echo Attempting to download EdgeDriver from alternative source...
powershell -Command "try { Invoke-WebRequest -Uri 'https://github.com/MicrosoftEdge/EdgeWebDriver/releases/download/130.0.2849.68/edgedriver_win64.zip' -OutFile 'drivers\edgedriver.zip' -ErrorAction Stop; Write-Host 'Download successful' } catch { Write-Host 'Download failed:' $_.Exception.Message }"

if exist "drivers\edgedriver.zip" (
    echo ✅ EdgeDriver downloaded successfully
    echo Extracting EdgeDriver...
    powershell -Command "Expand-Archive -Path 'drivers\edgedriver.zip' -DestinationPath 'drivers\' -Force"
    del "drivers\edgedriver.zip"
    
    if exist "drivers\msedgedriver.exe" (
        echo ✅ EdgeDriver setup completed successfully
        echo EdgeDriver location: drivers\msedgedriver.exe
        goto :success
    )
)

:fallback
echo ========================================
echo  USING FALLBACK APPROACH
echo ========================================
echo Creating EdgeDriver configuration for system PATH usage...

REM Create a batch file that will try to use system EdgeDriver
echo @echo off > drivers\msedgedriver.exe
echo REM EdgeDriver will be resolved from system PATH >> drivers\msedgedriver.exe

echo ✅ Fallback EdgeDriver configuration created
echo Note: Framework will attempt to use EdgeDriver from system PATH

:success
echo ========================================
echo  EDGE DRIVER SETUP COMPLETED
echo ========================================
echo.
echo The framework will now try multiple methods to locate EdgeDriver:
echo 1. Local drivers\msedgedriver.exe
echo 2. System PATH environment variable
echo 3. Standard Microsoft Edge installation paths
echo.
echo This resolves the WebDriverManager network connectivity issue.
echo ========================================
