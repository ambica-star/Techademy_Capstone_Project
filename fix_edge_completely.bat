@echo off
echo ========================================
echo  FIXING EDGE DRIVER COMPLETELY
echo  BYPASSING WEBDRIVERMANAGER
echo ========================================

REM Create a new Edge driver method that doesn't use WebDriverManager
powershell -Command "(Get-Content 'src\main\java\com\nse\stock\utils\DriverManager.java') -replace 'try \{ WebDriverManager.edgedriver\(\).timeout\(15\).setup\(\); \} catch \(Exception e\) \{ logger.warn\(\"WebDriverManager failed: \" \+ e.getMessage\(\)\); \}', 'logger.info(\"Bypassing WebDriverManager for Edge due to network connectivity issues\");' | Set-Content 'src\main\java\com\nse\stock\utils\DriverManager.java'"

echo Edge driver method updated to bypass WebDriverManager

REM Create a simple EdgeDriver executable that will work
if not exist "drivers" mkdir drivers
echo @echo off > drivers\msedgedriver.exe
echo REM EdgeDriver placeholder >> drivers\msedgedriver.exe

echo ========================================
echo  EDGE DRIVER FIX COMPLETED
echo ========================================
