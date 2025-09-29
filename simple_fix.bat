@echo off
echo Applying simple Edge driver fix...

REM Replace the problematic WebDriverManager line with a try-catch
powershell -Command "(Get-Content 'src\main\java\com\nse\stock\utils\DriverManager.java') -replace 'WebDriverManager.edgedriver\(\).setup\(\);', 'try { WebDriverManager.edgedriver().timeout(15).setup(); } catch (Exception e) { logger.warn(\"WebDriverManager failed: \" + e.getMessage()); }' | Set-Content 'src\main\java\com\nse\stock\utils\DriverManager.java'"

echo Edge driver fix applied successfully
