package com.nse.stock.utils;

import com.nse.stock.listeners.ExtentReportListener;
import com.nse.stock.models.StockInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for generating additional reports and summaries
 * Complements ExtentReports with custom reporting functionality
 */
public class ReportUtils {
    
    private static final Logger logger = LogManager.getLogger(ReportUtils.class);
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Generate CSV report for stock information
     * @param stockInfoList List of stock information
     * @param fileName CSV file name
     */
    public static void generateStockCSVReport(List<StockInfo> stockInfoList, String fileName) {
        try {
            // Create reports directory
            File reportsDir = new File("test-output/reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            String timestamp = LocalDateTime.now().format(formatter);
            String csvFileName = String.format("%s_%s.csv", fileName, timestamp);
            File csvFile = new File(reportsDir, csvFileName);
            
            try (FileWriter writer = new FileWriter(csvFile)) {
                // Write CSV header
                writer.append("Symbol,Company Name,Current Price,Price Change,Percentage Change,")
                      .append("52 Week High,52 Week Low,Volume,Market Cap,Purchase Price,")
                      .append("Profit/Loss,Profit/Loss %,Status\n");
                
                // Write stock data
                for (StockInfo stock : stockInfoList) {
                    writer.append(String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%s,%.2f,%.2f,%.2f,%s\n",
                        stock.getSymbol(),
                        stock.getCompanyName(),
                        stock.getCurrentPrice(),
                        stock.getPriceChange(),
                        stock.getPercentageChange(),
                        stock.getWeekHigh52(),
                        stock.getWeekLow52(),
                        stock.getVolume(),
                        stock.getMarketCap(),
                        stock.getPurchasePrice(),
                        stock.getProfitLoss(),
                        stock.getProfitLossPercentage(),
                        stock.getProfitLossStatus()
                    ));
                }
                
                logger.info("CSV report generated: {}", csvFile.getAbsolutePath());
            }
            
        } catch (IOException e) {
            logger.error("Error generating CSV report: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Generate summary report for test execution
     * @param testResults Test execution results
     */
    public static void generateTestSummaryReport(TestExecutionSummary testResults) {
        try {
            File reportsDir = new File("test-output/reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            String timestamp = LocalDateTime.now().format(formatter);
            String summaryFileName = String.format("test_summary_%s.txt", timestamp);
            File summaryFile = new File(reportsDir, summaryFileName);
            
            try (FileWriter writer = new FileWriter(summaryFile)) {
                writer.append("NSE STOCK TESTING EXECUTION SUMMARY\n");
                writer.append("=====================================\n\n");
                
                writer.append(String.format("Execution Date: %s\n", 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
                writer.append(String.format("Total Tests: %d\n", testResults.getTotalTests()));
                writer.append(String.format("Passed Tests: %d\n", testResults.getPassedTests()));
                writer.append(String.format("Failed Tests: %d\n", testResults.getFailedTests()));
                writer.append(String.format("Skipped Tests: %d\n", testResults.getSkippedTests()));
                writer.append(String.format("Success Rate: %.2f%%\n", testResults.getSuccessRate()));
                writer.append(String.format("Execution Time: %d ms\n", testResults.getExecutionTime()));
                
                writer.append("\nBROWSER COVERAGE:\n");
                writer.append("-----------------\n");
                for (String browser : testResults.getBrowsersCovered()) {
                    writer.append(String.format("- %s\n", browser));
                }
                
                if (!testResults.getFailedTestNames().isEmpty()) {
                    writer.append("\nFAILED TESTS:\n");
                    writer.append("-------------\n");
                    for (String failedTest : testResults.getFailedTestNames()) {
                        writer.append(String.format("- %s\n", failedTest));
                    }
                }
                
                logger.info("Test summary report generated: {}", summaryFile.getAbsolutePath());
            }
            
        } catch (IOException e) {
            logger.error("Error generating test summary report: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Log stock information to ExtentReports
     * @param stockInfo Stock information to log
     */
    public static void logStockInfoToReport(StockInfo stockInfo) {
        if (stockInfo != null) {
            ExtentReportListener.logInfo("Stock Information Details:");
            ExtentReportListener.logInfo("Symbol: " + stockInfo.getSymbol());
            ExtentReportListener.logInfo("Company: " + stockInfo.getCompanyName());
            ExtentReportListener.logInfo("Current Price: " + stockInfo.getFormattedCurrentPrice());
            ExtentReportListener.logInfo("Price Change: ₹" + stockInfo.getPriceChange());
            ExtentReportListener.logInfo("Percentage Change: " + stockInfo.getPercentageChange() + "%");
            
            if (stockInfo.has52WeekData()) {
                ExtentReportListener.logInfo("52-Week Range: " + stockInfo.getFormatted52WeekRange());
            }
            
            if (stockInfo.getPurchasePrice() > 0) {
                ExtentReportListener.logInfo("Purchase Price: ₹" + stockInfo.getPurchasePrice());
                ExtentReportListener.logInfo("Profit/Loss: " + stockInfo.getFormattedProfitLoss());
                ExtentReportListener.logInfo("Status: " + stockInfo.getProfitLossStatus());
            }
            
            ExtentReportListener.logInfo("Volume: " + stockInfo.getVolume());
            ExtentReportListener.logInfo("Market Cap: " + stockInfo.getMarketCap());
        }
    }
    
    /**
     * Log profit/loss calculation details to report
     * @param stockInfo Stock information with profit/loss data
     */
    public static void logProfitLossCalculation(StockInfo stockInfo) {
        if (stockInfo != null && stockInfo.getPurchasePrice() > 0) {
            ExtentReportListener.logInfo("Profit/Loss Calculation:");
            ExtentReportListener.logInfo("Purchase Price: ₹" + stockInfo.getPurchasePrice());
            ExtentReportListener.logInfo("Current Price: " + stockInfo.getFormattedCurrentPrice());
            ExtentReportListener.logInfo("Difference: ₹" + stockInfo.getProfitLoss());
            ExtentReportListener.logInfo("Percentage: " + String.format("%.2f%%", stockInfo.getProfitLossPercentage()));
            ExtentReportListener.logInfo("Status: " + stockInfo.getProfitLossStatus());
            
            if (stockInfo.isProfit()) {
                ExtentReportListener.logInfo("✅ Stock is in PROFIT");
            } else if (stockInfo.isLoss()) {
                ExtentReportListener.logWarning("❌ Stock is in LOSS");
            } else {
                ExtentReportListener.logInfo("➖ Stock is at BREAK-EVEN");
            }
        }
    }
    
    /**
     * Log 52-week analysis to report
     * @param stockInfo Stock information with 52-week data
     */
    public static void log52WeekAnalysis(StockInfo stockInfo) {
        if (stockInfo != null && stockInfo.has52WeekData()) {
            double currentPrice = stockInfo.getCurrentPrice();
            double weekHigh = stockInfo.getWeekHigh52();
            double weekLow = stockInfo.getWeekLow52();
            
            ExtentReportListener.logInfo("52-Week Analysis:");
            ExtentReportListener.logInfo("Current Price: " + stockInfo.getFormattedCurrentPrice());
            ExtentReportListener.logInfo("52-Week High: ₹" + weekHigh);
            ExtentReportListener.logInfo("52-Week Low: ₹" + weekLow);
            ExtentReportListener.logInfo("Range: " + stockInfo.getFormatted52WeekRange());
            
            // Calculate position in range
            double range = weekHigh - weekLow;
            double positionFromLow = currentPrice - weekLow;
            double positionPercentage = (positionFromLow / range) * 100;
            
            ExtentReportListener.logInfo(String.format("Position in Range: %.2f%%", positionPercentage));
            
            // Distance from high and low
            double distanceFromHigh = weekHigh - currentPrice;
            double distanceFromLow = currentPrice - weekLow;
            
            ExtentReportListener.logInfo(String.format("Distance from High: ₹%.2f", distanceFromHigh));
            ExtentReportListener.logInfo(String.format("Distance from Low: ₹%.2f", distanceFromLow));
            
            // Analysis
            if (positionPercentage > 80) {
                ExtentReportListener.logInfo("📈 Stock is near 52-week HIGH");
            } else if (positionPercentage < 20) {
                ExtentReportListener.logWarning("📉 Stock is near 52-week LOW");
            } else {
                ExtentReportListener.logInfo("📊 Stock is in middle range");
            }
        } else {
            ExtentReportListener.logWarning("52-week data not available for analysis");
        }
    }
    
    /**
     * Create test execution summary
     * @param totalTests Total number of tests
     * @param passedTests Number of passed tests
     * @param failedTests Number of failed tests
     * @param skippedTests Number of skipped tests
     * @param executionTime Total execution time in milliseconds
     * @return TestExecutionSummary object
     */
    public static TestExecutionSummary createExecutionSummary(int totalTests, int passedTests, 
                                                             int failedTests, int skippedTests, 
                                                             long executionTime) {
        return new TestExecutionSummary(totalTests, passedTests, failedTests, skippedTests, executionTime);
    }
    
    /**
     * Inner class to hold test execution summary data
     */
    public static class TestExecutionSummary {
        private final int totalTests;
        private final int passedTests;
        private final int failedTests;
        private final int skippedTests;
        private final long executionTime;
        private final List<String> browsersCovered;
        private final List<String> failedTestNames;
        
        public TestExecutionSummary(int totalTests, int passedTests, int failedTests, 
                                   int skippedTests, long executionTime) {
            this.totalTests = totalTests;
            this.passedTests = passedTests;
            this.failedTests = failedTests;
            this.skippedTests = skippedTests;
            this.executionTime = executionTime;
            this.browsersCovered = Arrays.asList("Chrome", "Firefox", "Edge");
            this.failedTestNames = Arrays.asList(); // Would be populated with actual failed test names
        }
        
        public double getSuccessRate() {
            return totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
        }
        
        // Getters
        public int getTotalTests() { return totalTests; }
        public int getPassedTests() { return passedTests; }
        public int getFailedTests() { return failedTests; }
        public int getSkippedTests() { return skippedTests; }
        public long getExecutionTime() { return executionTime; }
        public List<String> getBrowsersCovered() { return browsersCovered; }
        public List<String> getFailedTestNames() { return failedTestNames; }
    }
}
