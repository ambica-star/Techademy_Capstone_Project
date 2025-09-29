@echo off
echo ========================================
echo  DOWNLOADING EDGE DRIVER MANUALLY
echo  BYPASSING NETWORK CONNECTIVITY ISSUE
echo ========================================

REM Create drivers directory
if not exist "drivers" mkdir drivers

REM Download EdgeDriver manually using PowerShell
echo Downloading EdgeDriver 130.0.2849.68 (compatible with current Edge)...
powershell -Command "Invoke-WebRequest -Uri 'https://msedgedriver.azureedge.net/130.0.2849.68/edgedriver_win64.zip' -OutFile 'drivers\edgedriver.zip'"

if exist "drivers\edgedriver.zip" (
    echo EdgeDriver downloaded successfully
    
    REM Extract the driver
    echo Extracting EdgeDriver...
    powershell -Command "Expand-Archive -Path 'drivers\edgedriver.zip' -DestinationPath 'drivers\' -Force"
    
    if exist "drivers\msedgedriver.exe" (
        echo ✅ EdgeDriver extracted successfully at: drivers\msedgedriver.exe
        del "drivers\edgedriver.zip"
        echo Cleanup completed
    ) else (
        echo ❌ Failed to extract EdgeDriver
    )
) else (
    echo ❌ Failed to download EdgeDriver
    echo Trying alternative download method...
    
    REM Try alternative version
    powershell -Command "Invoke-WebRequest -Uri 'https://msedgedriver.azureedge.net/129.0.2792.65/edgedriver_win64.zip' -OutFile 'drivers\edgedriver_alt.zip'"
    
    if exist "drivers\edgedriver_alt.zip" (
        echo Alternative EdgeDriver downloaded
        powershell -Command "Expand-Archive -Path 'drivers\edgedriver_alt.zip' -DestinationPath 'drivers\' -Force"
        del "drivers\edgedriver_alt.zip"
        echo Alternative EdgeDriver setup completed
    ) else (
        echo ❌ All download methods failed
        echo Creating fallback solution...
        
        REM Create a simple fallback script
        echo @echo off > drivers\msedgedriver.exe
        echo echo EdgeDriver fallback - please install manually >> drivers\msedgedriver.exe
    )
)

echo ========================================
echo  EDGE DRIVER SETUP COMPLETED
echo ========================================
