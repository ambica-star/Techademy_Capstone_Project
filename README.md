# 🎓 NSE Stock Testing Automation Framework - Capstone Project

[![Java](https://img.shields.io/badge/Java-8-orange.svg)](https://www.oracle.com/java/)
[![Selenium](https://img.shields.io/badge/Selenium-3.141.59-green.svg)](https://selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.4.0-blue.svg)](https://testng.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9.5-red.svg)](https://maven.apache.org/)
[![Cross-Browser](https://img.shields.io/badge/Cross--Browser-Chrome%20%7C%20Firefox%20%7C%20Edge-brightgreen.svg)](https://github.com/ambica-star/Techademy_Capstone_Project)

## 📋 Capstone Project Overview

This is a comprehensive **Selenium Java automation framework** designed for testing stock information display and calculations on the **NSE India website**. The framework demonstrates enterprise-grade automation capabilities with **parallel cross-browser testing**, **professional reporting**, and **real-time stock data validation** as required for the capstone project demonstration.

## Features

- ✅ **Stock Information Verification**: Automated extraction and validation of stock data
- ✅ **Profit/Loss Calculation**: Dynamic calculation based on purchase vs current prices
- ✅ **52-Week High/Low Analysis**: Extraction and verification of 52-week price ranges
- ✅ **Multi-Browser Support**: Chrome, Firefox, and Edge browser compatibility
- ✅ **Parallel Execution**: Concurrent test execution across multiple browsers
- ✅ **Comprehensive Reporting**: ExtentReports with screenshots and detailed logs
- ✅ **Data-Driven Testing**: JSON-based test data management
- ✅ **Retry Mechanism**: Automatic retry for flaky tests
- ✅ **Page Object Model**: Maintainable and scalable test architecture

## 🛠️ Technology Stack

- **Java 8** - Programming language with enterprise compatibility
- **Selenium WebDriver 3.141.59** - Stable browser automation framework
- **TestNG 7.4.0** - Testing framework with parallel execution capabilities
- **Maven 3.9.5** - Build and dependency management
- **WebDriverManager 5.6.2** - Latest automatic driver management (Chrome 140+ support)
- **ExtentReports 4.1.7** - Professional HTML reporting with screenshots
- **Log4j2 2.17.2** - Comprehensive logging framework
- **Jackson 2.13.4** - JSON data handling for test data

## 🚀 Quick Start - Capstone Demo

### Prerequisites
- Java 8 installed and configured
- Maven 3.9.5 (included in tools/ directory)
- Chrome, Firefox, and Edge browsers installed

### Execution Commands

#### Individual Browser Testing
```bash
# Firefox Browser (Primary Demo)
.\run_firefox.bat

# Chrome Browser (Secondary Demo)
.\run_chrome.bat

# Edge Browser (Tertiary Demo)
.\run_edge.bat
```

#### Cross-Browser Parallel Testing
```bash
# All Browsers Parallel Execution (Capstone Demo)
.\run_all_browsers.bat
```

### Expected Artifacts
After execution, the following artifacts will be generated:
- **Screenshots**: `src/reports/screenshots/` - High-quality execution evidence
- **HTML Reports**: `src/reports/html/ExtentReport.html` - Comprehensive test reports
- **TestNG Reports**: `target/surefire-reports/` - Standard TestNG HTML reports
- **Logs**: `src/reports/logs/automation.log` - Detailed execution logs

## Technology Stack

- **Java 11+**: Programming language
- **Selenium WebDriver 4.15.0**: Browser automation
- **TestNG 7.8.0**: Testing framework
- **Maven**: Build and dependency management
- **WebDriverManager 5.6.2**: Automatic driver management
- **ExtentReports 5.1.1**: Test reporting
- **Log4j 2.21.1**: Logging framework
- **Jackson**: JSON data handling

## Project Structure

```
NSC_stock_capstone_project/
├── src/
│   ├── main/
│   │   ├── java/com/nse/stock/
│   │   │   ├── base/           # Base test classes
│   │   │   ├── models/         # Data models
│   │   │   ├── pages/          # Page Object Model classes
│   │   │   ├── utils/          # Utility classes
│   │   │   └── listeners/      # TestNG listeners
│   │   └── resources/
│   │       ├── config.properties
│   │       └── log4j2.xml
│   └── test/
│       ├── java/com/nse/stock/tests/  # Test classes
│       └── resources/
│           ├── testdata/       # Test data files
│           ├── testng.xml      # TestNG configuration
│           ├── testng-parallel.xml
│           └── testng-smoke.xml
├── test-output/               # Generated reports and logs
├── pom.xml                   # Maven configuration
└── README.md                 # This file
```

## Prerequisites

### Software Requirements

1. **Java Development Kit (JDK) 11 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Set JAVA_HOME environment variable

2. **Apache Maven 3.6+**
   - Download from [Maven Official Site](https://maven.apache.org/download.cgi)
   - Add Maven bin directory to PATH

3. **Web Browsers**
   - Google Chrome (latest version)
   - Mozilla Firefox (latest version)
   - Microsoft Edge (latest version)

4. **IDE (Optional but recommended)**
   - IntelliJ IDEA
   - Eclipse
   - Visual Studio Code

### Browser Drivers

The framework uses WebDriverManager to automatically download and manage browser drivers. No manual driver setup required!

## Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd NSC_stock_capstone_project
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Verify Installation

```bash
mvn test -Dtest=StockInformationTest#testNSEWebsiteAccessibility
```

## Configuration

### config.properties

Key configuration options in `src/main/resources/config.properties`:

```properties
# Browser Configuration
browser=chrome
headless=false

# Timeouts (seconds)
implicit.wait=10
explicit.wait=20
page.load.timeout=30

# URLs
nse.base.url=https://www.nseindia.com/

# Test Data
default.stock.symbol=TATAMOTORS
test.stocks=TATAMOTORS,RELIANCE,INFY,TCS,HDFCBANK

# Reporting
screenshot.on.failure=true
extent.report.path=test-output/reports/ExtentReport.html

# Parallel Execution
parallel.browsers=chrome,firefox,edge
max.retry.count=2
```

## Test Execution

### 1. Run All Tests (Default)

```bash
mvn test
```

### 2. Run Smoke Tests

```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml
```

### 3. Run Parallel Tests

```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-parallel.xml
```

### 4. Run Specific Browser

```bash
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

### 5. Run Specific Test Class

```bash
mvn test -Dtest=StockInformationTest
mvn test -Dtest=StockProfitLossTest
mvn test -Dtest=Stock52WeekHighLowTest
```

### 6. Run Specific Test Method

```bash
mvn test -Dtest=StockInformationTest#testDefaultStockInformation
```

### 7. Run in Headless Mode

```bash
mvn test -Dheadless=true
```

## Test Cases

### 1. Stock Information Display Tests

- **testNSEWebsiteAccessibility**: Verifies NSE website is accessible
- **testStockInformationDisplay**: Validates stock information display for multiple stocks
- **testDefaultStockInformation**: Tests default stock (TATAMOTORS) information
- **testInvalidStockSearch**: Tests behavior with invalid stock symbols
- **testStockInformationCompleteness**: Verifies completeness of extracted data

### 2. Profit/Loss Calculation Tests

- **testStockProfitLossCalculation**: Calculates profit/loss based on predefined purchase prices
- **testTataMotorsProfitLoss**: Specific test for TATAMOTORS profit/loss
- **testCustomPurchasePriceProfitLoss**: Tests with custom purchase prices
- **testBatchProfitLossCalculation**: Batch processing of multiple stocks
- **testProfitLossEdgeCases**: Edge case testing for calculations

### 3. 52-Week High/Low Tests

- **test52WeekHighLowExtraction**: Extracts and verifies 52-week data
- **testTataMotors52WeekData**: Specific 52-week data test for TATAMOTORS
- **test52WeekDataAvailability**: Checks data availability across stocks
- **test52WeekDataConsistency**: Validates logical consistency of data
- **test52WeekExtractionMethods**: Tests different extraction approaches

## Test Data

### Stock Data (stocks.json)

The framework includes predefined test data for NIFTY 50 stocks:

```json
{
  "nifty50_stocks": [
    {
      "symbol": "TATAMOTORS",
      "companyName": "Tata Motors Limited",
      "purchasePrice": 500.00,
      "expectedSector": "Automobile and Auto Components"
    }
  ]
}
```

### Adding New Test Data

1. Edit `src/test/resources/testdata/stocks.json`
2. Add new stock entries with symbol, company name, and purchase price
3. Update test data provider methods if needed

## Reporting

### ExtentReports

- **Location**: `test-output/reports/ExtentReport.html`
- **Features**: 
  - Test execution summary
  - Screenshots on failure
  - Browser-wise test results
  - Detailed logs and stack traces

### Logs

- **Location**: `test-output/logs/automation.log`
- **Levels**: INFO, WARN, ERROR, DEBUG
- **Rolling**: Daily rotation with size-based triggers

### Screenshots

- **Location**: `test-output/screenshots/`
- **Naming**: `{STATUS}_{CLASS}_{METHOD}_{BROWSER}_{TIMESTAMP}.png`
- **Triggers**: Test failures (configurable for success)

## Parallel Execution

The framework supports parallel execution across:

- **Multiple Browsers**: Chrome, Firefox, Edge
- **Multiple Tests**: Concurrent test method execution
- **Thread Safety**: ThreadLocal WebDriver management

### Configuration

```xml
<suite name="Parallel Suite" parallel="tests" thread-count="3">
  <test name="Chrome Tests" parallel="methods" thread-count="2">
    <parameter name="browser" value="chrome"/>
  </test>
</suite>
```

## Troubleshooting

### Common Issues

1. **Browser Driver Issues**
   - Solution: WebDriverManager handles this automatically
   - Manual fix: Update browser to latest version

2. **Element Not Found**
   - Check if NSE website structure changed
   - Verify locators in page object classes
   - Increase wait times in config.properties

3. **Test Failures Due to Network**
   - Enable retry mechanism (already configured)
   - Check internet connectivity
   - Verify NSE website accessibility

4. **Memory Issues**
   - Reduce parallel thread count
   - Increase JVM heap size: `-Xmx2g`

### Debug Mode

```bash
mvn test -Dlog.level=DEBUG -Dheadless=false
```

## Best Practices

1. **Page Object Model**: All page interactions through page objects
2. **Data-Driven**: Test data externalized in JSON files
3. **Wait Strategies**: Explicit waits over implicit waits
4. **Error Handling**: Comprehensive exception handling
5. **Logging**: Detailed logging at appropriate levels
6. **Screenshots**: Automatic capture on failures
7. **Retry Logic**: Automatic retry for flaky tests

## Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit pull request

## Support

For issues and questions:

1. Check existing documentation
2. Review logs in `test-output/logs/`
3. Check ExtentReports for detailed test results
4. Create issue with detailed description and logs

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Note**: This framework is designed for educational and testing purposes. Ensure compliance with NSE India's terms of service when using this automation framework.
