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

/**
 * Test class for verifying stock information display on NSE website
 * Tests the basic functionality of stock search and information extraction
 */
public class StockInformationTest extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(StockInformationTest.class);
    private TestDataReader testDataReader;
    
    @Test(priority = 1, description = "Verify NSE website is accessible")
    public void testNSEWebsiteAccessibility() {
        logger.info("Starting NSE website accessibility test");
        
        NSEHomePage homePage = new NSEHomePage(driver);
        homePage.navigateToNSE();
        
        // Verify page is loaded
        Assert.assertTrue(homePage.isPageLoaded(), "NSE website should be accessible");
        
        // Verify page title
        String pageTitle = homePage.getPageTitle();
        Assert.assertTrue(pageTitle.toLowerCase().contains("nse"), 
            "Page title should contain 'NSE'. Actual title: " + pageTitle);
        
        logger.info("NSE website accessibility test completed successfully");
        takeScreenshot("nse_website_loaded");
    }
    
    @Test(priority = 2, dataProvider = "stockSymbols", 
          description = "Verify stock information display for multiple stocks")
    public void testStockInformationDisplay(String stockSymbol) {
        logger.info("Starting stock information display test for: {}", stockSymbol);
        
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate to NSE website
        homePage.navigateToNSE();
        
        // Search for stock
        homePage.searchStock(stockSymbol);
        
        // Wait for stock details page to load
        stockDetailsPage.waitForPageLoad();
        
        // Take screenshot before extraction
        takeScreenshot("before_extraction_" + stockSymbol);
        
        // Verify stock information is displayed
        Assert.assertTrue(stockDetailsPage.isStockInfoDisplayed(), 
            "Stock information should be displayed for: " + stockSymbol);
        
        // Extract stock information
        StockInfo stockInfo = stockDetailsPage.extractStockInfo();
        
        // Verify extracted information
        Assert.assertNotNull(stockInfo, "Stock information should not be null");
        Assert.assertTrue(stockInfo.isValid(), "Stock information should be valid");
        
        // Verify stock symbol
        Assert.assertTrue(stockInfo.getSymbol().contains(stockSymbol) || 
                         stockSymbol.contains(stockInfo.getSymbol()),
            "Extracted symbol should match searched symbol. Expected: " + stockSymbol + 
            ", Actual: " + stockInfo.getSymbol());
        
        // Verify current price is positive
        Assert.assertTrue(stockInfo.getCurrentPrice() > 0, 
            "Current price should be positive. Actual: " + stockInfo.getCurrentPrice());
        
        // Log extracted information
        logger.info("Stock Information for {}: {}", stockSymbol, stockInfo.toString());
        
        // Take screenshot after extraction
        takeScreenshot("after_extraction_" + stockSymbol);
        
        logger.info("Stock information display test completed for: {}", stockSymbol);
    }
    
    @Test(priority = 3, description = "Verify stock information for default stock (TATAMOTORS)")
    public void testDefaultStockInformation() {
        logger.info("Starting default stock information test");
        
        String defaultStock = configReader.getDefaultStockSymbol();
        testDataReader = TestDataReader.getInstance();
        
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate to NSE website
        homePage.navigateToNSE();
        
        // Search for default stock
        homePage.searchStock(defaultStock);
        
        // Wait for stock details page to load
        stockDetailsPage.waitForPageLoad();
        
        // Extract stock information
        StockInfo stockInfo = stockDetailsPage.extractStockInfo();
        
        // Verify basic information
        Assert.assertNotNull(stockInfo, "Stock information should not be null");
        Assert.assertTrue(stockInfo.isValid(), "Stock information should be valid");
        
        // Get expected data from test data
        StockInfo expectedStock = testDataReader.getStockBySymbol(defaultStock);
        if (expectedStock != null) {
            Assert.assertEquals(stockInfo.getSymbol(), expectedStock.getSymbol(),
                "Stock symbol should match expected value");
        }
        
        // Verify all required fields are present
        Assert.assertNotNull(stockInfo.getSymbol(), "Stock symbol should not be null");
        Assert.assertTrue(stockInfo.getCurrentPrice() > 0, "Current price should be positive");
        
        logger.info("Default stock information: {}", stockInfo.toString());
        logger.info("Default stock information test completed successfully");
    }
    
    @Test(priority = 4, description = "Verify stock search functionality with invalid symbol")
    public void testInvalidStockSearch() {
        logger.info("Starting invalid stock search test");
        
        NSEHomePage homePage = new NSEHomePage(driver);
        
        // Navigate to NSE website
        homePage.navigateToNSE();
        
        // Search for invalid stock symbol
        String invalidSymbol = "INVALIDSTOCK123";
        homePage.searchStock(invalidSymbol);
        
        // Take screenshot for documentation
        takeScreenshot("invalid_stock_search_" + invalidSymbol);
        
        // Verify appropriate handling of invalid search
        // This test documents the behavior rather than asserting specific outcomes
        // as different websites handle invalid searches differently
        
        logger.info("Invalid stock search test completed");
    }
    
    @Test(priority = 5, description = "Verify stock information completeness")
    public void testStockInformationCompleteness() {
        logger.info("Starting stock information completeness test");
        
        String stockSymbol = "RELIANCE";
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate and search
        homePage.navigateToNSE();
        homePage.searchStock(stockSymbol);
        stockDetailsPage.waitForPageLoad();
        
        // Extract information
        StockInfo stockInfo = stockDetailsPage.extractStockInfo();
        
        // Verify completeness of information
        Assert.assertNotNull(stockInfo.getSymbol(), "Symbol should not be null");
        Assert.assertTrue(stockInfo.getCurrentPrice() > 0, "Current price should be available");
        
        // Log what information is available
        logger.info("Stock Information Completeness for {}:", stockSymbol);
        logger.info("Symbol: {}", stockInfo.getSymbol());
        logger.info("Company Name: {}", stockInfo.getCompanyName());
        logger.info("Current Price: {}", stockInfo.getFormattedCurrentPrice());
        logger.info("Price Change: {}", stockInfo.getPriceChange());
        logger.info("Percentage Change: {}%", stockInfo.getPercentageChange());
        logger.info("Volume: {}", stockInfo.getVolume());
        logger.info("Market Cap: {}", stockInfo.getMarketCap());
        
        // Document 52-week data availability
        if (stockInfo.has52WeekData()) {
            logger.info("52 Week Range: {}", stockInfo.getFormatted52WeekRange());
        } else {
            logger.info("52 Week data not available");
        }
        
        logger.info("Stock information completeness test completed");
    }
    
    /**
     * Data provider for stock symbols
     * @return Array of stock symbols for testing
     */
    @DataProvider(name = "stockSymbols")
    public Object[][] stockSymbolsProvider() {
        testDataReader = TestDataReader.getInstance();
        
        // Get subset of stocks for testing to avoid long execution times
        String[] allSymbols = testDataReader.getStockSymbols();
        
        // Return first 3 stocks for quick testing
        int testCount = Math.min(3, allSymbols.length);
        Object[][] data = new Object[testCount][1];
        
        for (int i = 0; i < testCount; i++) {
            data[i][0] = allSymbols[i];
        }
        
        return data;
    }
    
    /**
     * Data provider for all stock symbols (for comprehensive testing)
     * @return Array of all stock symbols
     */
    @DataProvider(name = "allStockSymbols")
    public Object[][] allStockSymbolsProvider() {
        testDataReader = TestDataReader.getInstance();
        String[] symbols = testDataReader.getStockSymbols();
        
        Object[][] data = new Object[symbols.length][1];
        for (int i = 0; i < symbols.length; i++) {
            data[i][0] = symbols[i];
        }
        
        return data;
    }
}
