@echo off
powershell -Command "(Get-Content 'src\main\java\com\nse\stock\utils\DriverManager.java') -replace 'DriverManagerFixed', 'DriverManager' | Set-Content 'src\main\java\com\nse\stock\utils\DriverManager.java'"
echo Class name fixed successfully
