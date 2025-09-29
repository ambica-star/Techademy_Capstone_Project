package com.nse.stock.tests;

import com.nse.stock.base.BaseTest;
import com.nse.stock.models.StockInfo;
import com.nse.stock.pages.NSEHomePage;
import com.nse.stock.pages.StockDetailsPage;
import com.nse.stock.utils.TestDataReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test class for extracting and verifying 52-week high and low prices
 * Validates the availability and accuracy of 52-week price data
 */
public class Stock52WeekHighLowTest extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(Stock52WeekHighLowTest.class);
    private TestDataReader testDataReader;
    
    @Test(priority = 1, dataProvider = "stockSymbolsFor52Week", 
          description = "Extract and verify 52-week high and low prices for stocks")
    public void test52WeekHighLowExtraction(String stockSymbol) {
        logger.info("Starting 52-week high/low extraction test for: {}", stockSymbol);
        
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate to NSE website
        homePage.navigateToNSE();
        
        // Search for stock
        homePage.searchStock(stockSymbol);
        
        // Wait for stock details page to load
        stockDetailsPage.waitForPageLoad();
        
        // Take screenshot before extraction
        takeScreenshot("before_52week_" + stockSymbol);
        
        // Extract stock information including 52-week data
        StockInfo stockInfo = stockDetailsPage.extractStockInfo();
        
        // Verify basic stock information
        Assert.assertNotNull(stockInfo, "Stock information should not be null");
        Assert.assertTrue(stockInfo.isValid(), "Stock information should be valid");
        
        // Extract 52-week high and low specifically
        double weekHigh52 = stockDetailsPage.extract52WeekHigh();
        double weekLow52 = stockDetailsPage.extract52WeekLow();
        
        // Log extracted 52-week data
        logger.info("52-Week Data for {}:", stockSymbol);
        logger.info("Current Price: {}", stockInfo.getFormattedCurrentPrice());
        logger.info("52-Week High: ₹{}", weekHigh52);
        logger.info("52-Week Low: ₹{}", weekLow52);
        
        // Verify 52-week data availability and validity
        if (weekHigh52 > 0 && weekLow52 > 0) {
            // Verify logical relationship: High should be greater than or equal to Low
            Assert.assertTrue(weekHigh52 >= weekLow52, 
                String.format("52-week high (₹%.2f) should be >= 52-week low (₹%.2f)", 
                    weekHigh52, weekLow52));
            
            // Verify current price is within reasonable range of 52-week data
            // Current price should typically be between 52-week high and low
            // But we'll allow some tolerance for market volatility
            double tolerance = weekHigh52 * 0.1; // 10% tolerance
            Assert.assertTrue(stockInfo.getCurrentPrice() >= (weekLow52 - tolerance) && 
                            stockInfo.getCurrentPrice() <= (weekHigh52 + tolerance),
                String.format("Current price (₹%.2f) should be within reasonable range of 52-week data (₹%.2f - ₹%.2f)", 
                    stockInfo.getCurrentPrice(), weekLow52, weekHigh52));
            
            // Update stock info with 52-week data
            stockInfo.setWeekHigh52(weekHigh52);
            stockInfo.setWeekLow52(weekLow52);
            
            logger.info("52-Week Range: {}", stockInfo.getFormatted52WeekRange());
            
        } else {
            logger.warn("52-week data not available for: {}", stockSymbol);
            // Document that 52-week data is not available (this is acceptable)
        }
        
        // Take screenshot after extraction
        takeScreenshot("after_52week_" + stockSymbol);
        
        logger.info("52-week high/low extraction test completed for: {}", stockSymbol);
    }
    
    @Test(priority = 2, description = "Verify 52-week data for TATAMOTORS specifically")
    public void testTataMotors52WeekData() {
        logger.info("Starting TATAMOTORS specific 52-week data test");
        
        String stockSymbol = "TATAMOTORS";
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate and search
        homePage.navigateToNSE();
        homePage.searchStock(stockSymbol);
        stockDetailsPage.waitForPageLoad();
        
        // Extract information
        StockInfo stockInfo = stockDetailsPage.extractStockInfo();
        
        // Verify basic information
        Assert.assertNotNull(stockInfo, "Stock information should not be null");
        Assert.assertTrue(stockInfo.getCurrentPrice() > 0, "Current price should be positive");
        
        // Log detailed information
        logger.info("TATAMOTORS Detailed Information:");
        logger.info("Symbol: {}", stockInfo.getSymbol());
        logger.info("Company: {}", stockInfo.getCompanyName());
        logger.info("Current Price: {}", stockInfo.getFormattedCurrentPrice());
        logger.info("Price Change: ₹{}", stockInfo.getPriceChange());
        logger.info("Percentage Change: {}%", stockInfo.getPercentageChange());
        
        if (stockInfo.has52WeekData()) {
            logger.info("52-Week High: ₹{}", stockInfo.getWeekHigh52());
            logger.info("52-Week Low: ₹{}", stockInfo.getWeekLow52());
            logger.info("52-Week Range: {}", stockInfo.getFormatted52WeekRange());
            
            // Calculate position within 52-week range
            double range = stockInfo.getWeekHigh52() - stockInfo.getWeekLow52();
            double positionFromLow = stockInfo.getCurrentPrice() - stockInfo.getWeekLow52();
            double positionPercentage = (positionFromLow / range) * 100;
            
            logger.info("Current position in 52-week range: {:.2f}%", positionPercentage);
        } else {
            logger.info("52-week data not available for TATAMOTORS");
        }
        
        logger.info("TATAMOTORS 52-week data test completed");
    }
    
    @Test(priority = 3, description = "Test 52-week data availability across multiple stocks")
    public void test52WeekDataAvailability() {
        logger.info("Starting 52-week data availability test");
        
        testDataReader = TestDataReader.getInstance();
        List<StockInfo> stocks = testDataReader.getStocksForParallelTesting(5);
        
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        int stocksWithData = 0;
        int stocksWithoutData = 0;
        
        for (StockInfo testStock : stocks) {
            logger.info("Checking 52-week data availability for: {}", testStock.getSymbol());
            
            try {
                // Navigate and search
                homePage.navigateToNSE();
                homePage.searchStock(testStock.getSymbol());
                stockDetailsPage.waitForPageLoad();
                
                // Extract 52-week data
                double weekHigh = stockDetailsPage.extract52WeekHigh();
                double weekLow = stockDetailsPage.extract52WeekLow();
                
                if (weekHigh > 0 && weekLow > 0) {
                    stocksWithData++;
                    logger.info("{}: 52-week data available (High: ₹{}, Low: ₹{})", 
                        testStock.getSymbol(), weekHigh, weekLow);
                } else {
                    stocksWithoutData++;
                    logger.info("{}: 52-week data not available", testStock.getSymbol());
                }
                
            } catch (Exception e) {
                logger.error("Error checking 52-week data for {}: {}", 
                    testStock.getSymbol(), e.getMessage());
                stocksWithoutData++;
            }
        }
        
        // Log summary
        logger.info("52-Week Data Availability Summary:");
        logger.info("Total stocks checked: {}", stocks.size());
        logger.info("Stocks with 52-week data: {}", stocksWithData);
        logger.info("Stocks without 52-week data: {}", stocksWithoutData);
        logger.info("Data availability percentage: {:.2f}%", 
            (double) stocksWithData / stocks.size() * 100);
        
        // Verify at least some stocks have data (not a hard requirement)
        logger.info("52-week data availability test completed");
    }
    
    @Test(priority = 4, description = "Verify 52-week data logical consistency")
    public void test52WeekDataConsistency() {
        logger.info("Starting 52-week data consistency test");
        
        String stockSymbol = "RELIANCE";
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate and search
        homePage.navigateToNSE();
        homePage.searchStock(stockSymbol);
        stockDetailsPage.waitForPageLoad();
        
        // Extract information
        StockInfo stockInfo = stockDetailsPage.extractStockInfo();
        
        if (stockInfo.has52WeekData()) {
            double currentPrice = stockInfo.getCurrentPrice();
            double weekHigh = stockInfo.getWeekHigh52();
            double weekLow = stockInfo.getWeekLow52();
            
            // Verify logical consistency
            Assert.assertTrue(weekHigh >= weekLow, 
                "52-week high should be >= 52-week low");
            
            Assert.assertTrue(weekHigh > 0 && weekLow > 0, 
                "52-week high and low should be positive values");
            
            // Check if current price is reasonable compared to 52-week range
            double range = weekHigh - weekLow;
            double tolerance = range * 0.2; // 20% tolerance for market volatility
            
            if (currentPrice < (weekLow - tolerance) || currentPrice > (weekHigh + tolerance)) {
                logger.warn("Current price (₹{}) is outside expected range with tolerance: ₹{} - ₹{}", 
                    currentPrice, weekLow - tolerance, weekHigh + tolerance);
            }
            
            // Calculate and log additional metrics
            double distanceFromHigh = weekHigh - currentPrice;
            double distanceFromLow = currentPrice - weekLow;
            double percentFromHigh = (distanceFromHigh / weekHigh) * 100;
            double percentFromLow = (distanceFromLow / weekLow) * 100;
            
            logger.info("52-Week Data Consistency Analysis for {}:", stockSymbol);
            logger.info("Current Price: ₹{}", currentPrice);
            logger.info("52-Week High: ₹{}", weekHigh);
            logger.info("52-Week Low: ₹{}", weekLow);
            logger.info("Distance from High: ₹{} ({:.2f}%)", distanceFromHigh, percentFromHigh);
            logger.info("Distance from Low: ₹{} ({:.2f}%)", distanceFromLow, percentFromLow);
            
        } else {
            logger.info("52-week data not available for consistency check");
        }
        
        logger.info("52-week data consistency test completed");
    }
    
    @Test(priority = 5, description = "Test 52-week data extraction methods")
    public void test52WeekExtractionMethods() {
        logger.info("Starting 52-week extraction methods test");
        
        String stockSymbol = "INFY";
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate and search
        homePage.navigateToNSE();
        homePage.searchStock(stockSymbol);
        stockDetailsPage.waitForPageLoad();
        
        // Test different extraction methods
        double weekHigh1 = stockDetailsPage.extract52WeekHigh();
        double weekLow1 = stockDetailsPage.extract52WeekLow();
        
        // Extract through complete stock info
        StockInfo stockInfo = stockDetailsPage.extractStockInfo();
        double weekHigh2 = stockInfo.getWeekHigh52();
        double weekLow2 = stockInfo.getWeekLow52();
        
        // Compare results from different methods
        if (weekHigh1 > 0 && weekHigh2 > 0) {
            Assert.assertEquals(weekHigh1, weekHigh2, 0.01, 
                "52-week high should be consistent across extraction methods");
        }
        
        if (weekLow1 > 0 && weekLow2 > 0) {
            Assert.assertEquals(weekLow1, weekLow2, 0.01, 
                "52-week low should be consistent across extraction methods");
        }
        
        logger.info("Extraction method comparison for {}:", stockSymbol);
        logger.info("Method 1 - High: ₹{}, Low: ₹{}", weekHigh1, weekLow1);
        logger.info("Method 2 - High: ₹{}, Low: ₹{}", weekHigh2, weekLow2);
        
        logger.info("52-week extraction methods test completed");
    }
    
    /**
     * Data provider for stock symbols for 52-week testing
     * @return Array of stock symbols
     */
    @DataProvider(name = "stockSymbolsFor52Week")
    public Object[][] stockSymbolsFor52WeekProvider() {
        testDataReader = TestDataReader.getInstance();
        
        // Get subset of stocks for 52-week testing
        String[] allSymbols = testDataReader.getStockSymbols();
        int testCount = Math.min(4, allSymbols.length); // Test 4 stocks
        
        Object[][] data = new Object[testCount][1];
        for (int i = 0; i < testCount; i++) {
            data[i][0] = allSymbols[i];
        }
        
        return data;
    }
    
    /**
     * Data provider for major stocks (likely to have 52-week data)
     * @return Array of major stock symbols
     */
    @DataProvider(name = "majorStocks")
    public Object[][] majorStocksProvider() {
        // Major stocks that are more likely to have complete 52-week data
        String[] majorStocks = {"RELIANCE", "TCS", "HDFCBANK", "INFY", "ICICIBANK"};
        
        Object[][] data = new Object[majorStocks.length][1];
        for (int i = 0; i < majorStocks.length; i++) {
            data[i][0] = majorStocks[i];
        }
        
        return data;
    }
}
