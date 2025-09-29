# PowerShell script to fix Edge driver method
$driverManagerPath = "src\main\java\com\nse\stock\utils\DriverManager.java"
$fixPath = "edge_driver_fix.java"

# Read the original file
$content = Get-Content $driverManagerPath -Raw

# Read the fix content
$fixContent = Get-Content $fixPath -Raw

# Find the start and end of the createEdgeDriver method
$startPattern = "private static WebDriver createEdgeDriver\(\) \{"
$endPattern = "return new EdgeDriver\(options\);\s*\n\s*\}"

# Replace the method
$newContent = $content -replace "(?s)(\s*\/\*\*\s*\*\s*Create Edge WebDriver with options.*?private static WebDriver createEdgeDriver\(\) \{.*?return new EdgeDriver\(options\);\s*\n\s*\})", $fixContent

# Write back to file
$newContent | Set-Content $driverManagerPath -NoNewline

Write-Host "Edge driver method fixed successfully"
