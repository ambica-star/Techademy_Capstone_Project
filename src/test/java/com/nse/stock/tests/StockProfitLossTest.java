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
 * Test class for calculating and verifying profit/loss of stocks
 * Based on predefined purchase prices vs current market prices
 */
public class StockProfitLossTest extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(StockProfitLossTest.class);
    private TestDataReader testDataReader;
    
    @Test(priority = 1, dataProvider = "stocksWithPurchasePrice", 
          description = "Calculate and verify profit/loss for stocks with predefined purchase prices")
    public void testStockProfitLossCalculation(StockInfo testStock) {
        logger.info("Starting profit/loss calculation test for: {}", testStock.getSymbol());
        
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate to NSE website
        homePage.navigateToNSE();
        
        // Search for stock
        homePage.searchStock(testStock.getSymbol());
        
        // Wait for stock details page to load
        stockDetailsPage.waitForPageLoad();
        
        // Take screenshot before calculation
        takeScreenshot("before_profit_loss_" + testStock.getSymbol());
        
        // Extract current stock information
        StockInfo currentStockInfo = stockDetailsPage.extractStockInfo();
        
        // Verify stock information is available
        Assert.assertNotNull(currentStockInfo, "Current stock information should not be null");
        Assert.assertTrue(currentStockInfo.isValid(), "Current stock information should be valid");
        Assert.assertTrue(currentStockInfo.getCurrentPrice() > 0, 
            "Current price should be positive: " + currentStockInfo.getCurrentPrice());
        
        // Set purchase price from test data
        currentStockInfo.setPurchasePrice(testStock.getPurchasePrice());
        
        // Calculate profit/loss (this is done automatically when setting purchase price)
        double profitLoss = currentStockInfo.getProfitLoss();
        double profitLossPercentage = currentStockInfo.getProfitLossPercentage();
        
        // Log calculation details
        logger.info("Profit/Loss Calculation for {}:", testStock.getSymbol());
        logger.info("Purchase Price: ₹{}", testStock.getPurchasePrice());
        logger.info("Current Price: {}", currentStockInfo.getFormattedCurrentPrice());
        logger.info("Profit/Loss: {}", currentStockInfo.getFormattedProfitLoss());
        logger.info("Status: {}", currentStockInfo.getProfitLossStatus());
        
        // Verify calculation accuracy
        double expectedProfitLoss = currentStockInfo.getCurrentPrice() - testStock.getPurchasePrice();
        double expectedPercentage = (expectedProfitLoss / testStock.getPurchasePrice()) * 100;
        
        Assert.assertEquals(profitLoss, expectedProfitLoss, 0.01, 
            "Calculated profit/loss should match expected value");
        Assert.assertEquals(profitLossPercentage, expectedPercentage, 0.01, 
            "Calculated percentage should match expected value");
        
        // Verify profit/loss status
        if (profitLoss > 0) {
            Assert.assertTrue(currentStockInfo.isProfit(), "Stock should be in profit");
            Assert.assertEquals(currentStockInfo.getProfitLossStatus(), "PROFIT", 
                "Status should be PROFIT");
        } else if (profitLoss < 0) {
            Assert.assertTrue(currentStockInfo.isLoss(), "Stock should be in loss");
            Assert.assertEquals(currentStockInfo.getProfitLossStatus(), "LOSS", 
                "Status should be LOSS");
        } else {
            Assert.assertEquals(currentStockInfo.getProfitLossStatus(), "BREAK_EVEN", 
                "Status should be BREAK_EVEN");
        }
        
        // Take screenshot after calculation
        takeScreenshot("after_profit_loss_" + testStock.getSymbol());
        
        logger.info("Profit/loss calculation test completed for: {}", testStock.getSymbol());
    }
    
    @Test(priority = 2, description = "Test profit/loss calculation for TATAMOTORS with specific purchase price")
    public void testTataMotorsProfitLoss() {
        logger.info("Starting TATAMOTORS specific profit/loss test");
        
        testDataReader = TestDataReader.getInstance();
        StockInfo tataMotors = testDataReader.getStockBySymbol("TATAMOTORS");
        
        if (tataMotors != null) {
            testStockProfitLossCalculation(tataMotors);
        } else {
            Assert.fail("TATAMOTORS test data not found");
        }
        
        logger.info("TATAMOTORS profit/loss test completed");
    }
    
    @Test(priority = 3, description = "Test profit/loss calculation with custom purchase price")
    public void testCustomPurchasePriceProfitLoss() {
        logger.info("Starting custom purchase price profit/loss test");
        
        String stockSymbol = "RELIANCE";
        double customPurchasePrice = 2000.00; // Custom purchase price
        
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        // Navigate and search
        homePage.navigateToNSE();
        homePage.searchStock(stockSymbol);
        stockDetailsPage.waitForPageLoad();
        
        // Extract current information
        StockInfo stockInfo = stockDetailsPage.extractStockInfo();
        
        // Set custom purchase price
        stockInfo.setPurchasePrice(customPurchasePrice);
        
        // Verify calculation
        Assert.assertTrue(stockInfo.getCurrentPrice() > 0, "Current price should be positive");
        
        double expectedProfitLoss = stockInfo.getCurrentPrice() - customPurchasePrice;
        Assert.assertEquals(stockInfo.getProfitLoss(), expectedProfitLoss, 0.01, 
            "Profit/loss should be calculated correctly");
        
        logger.info("Custom purchase price test - Symbol: {}, Purchase: ₹{}, Current: {}, P/L: {}", 
            stockSymbol, customPurchasePrice, stockInfo.getFormattedCurrentPrice(), 
            stockInfo.getFormattedProfitLoss());
        
        logger.info("Custom purchase price profit/loss test completed");
    }
    
    @Test(priority = 4, description = "Test profit/loss calculation for multiple stocks in batch")
    public void testBatchProfitLossCalculation() {
        logger.info("Starting batch profit/loss calculation test");
        
        testDataReader = TestDataReader.getInstance();
        List<StockInfo> stocks = testDataReader.getStocksForParallelTesting(3); // Test 3 stocks
        
        NSEHomePage homePage = new NSEHomePage(driver);
        StockDetailsPage stockDetailsPage = new StockDetailsPage(driver);
        
        int profitCount = 0;
        int lossCount = 0;
        int breakEvenCount = 0;
        
        for (StockInfo testStock : stocks) {
            logger.info("Processing stock: {}", testStock.getSymbol());
            
            try {
                // Navigate and search
                homePage.navigateToNSE();
                homePage.searchStock(testStock.getSymbol());
                stockDetailsPage.waitForPageLoad();
                
                // Extract and calculate
                StockInfo currentInfo = stockDetailsPage.extractStockInfo();
                currentInfo.setPurchasePrice(testStock.getPurchasePrice());
                
                // Count profit/loss status
                switch (currentInfo.getProfitLossStatus()) {
                    case "PROFIT":
                        profitCount++;
                        break;
                    case "LOSS":
                        lossCount++;
                        break;
                    case "BREAK_EVEN":
                        breakEvenCount++;
                        break;
                }
                
                logger.info("Stock: {}, Status: {}, P/L: {}", 
                    currentInfo.getSymbol(), currentInfo.getProfitLossStatus(), 
                    currentInfo.getFormattedProfitLoss());
                
            } catch (Exception e) {
                logger.error("Error processing stock {}: {}", testStock.getSymbol(), e.getMessage());
            }
        }
        
        // Log summary
        logger.info("Batch Profit/Loss Summary:");
        logger.info("Total stocks processed: {}", stocks.size());
        logger.info("Stocks in profit: {}", profitCount);
        logger.info("Stocks in loss: {}", lossCount);
        logger.info("Stocks at break-even: {}", breakEvenCount);
        
        // Verify at least some calculations were performed
        Assert.assertTrue((profitCount + lossCount + breakEvenCount) > 0, 
            "At least one stock should have been processed");
        
        logger.info("Batch profit/loss calculation test completed");
    }
    
    @Test(priority = 5, description = "Test edge cases for profit/loss calculation")
    public void testProfitLossEdgeCases() {
        logger.info("Starting profit/loss edge cases test");
        
        // Test with zero purchase price
        StockInfo stockInfo = new StockInfo("TEST", "Test Company", 100.0);
        stockInfo.setPurchasePrice(0.0);
        
        // Should handle zero purchase price gracefully
        Assert.assertEquals(stockInfo.getProfitLoss(), 0.0, "Profit/loss should be 0 for zero purchase price");
        
        // Test with same purchase and current price
        stockInfo.setPurchasePrice(100.0);
        Assert.assertEquals(stockInfo.getProfitLoss(), 0.0, "Profit/loss should be 0 for same prices");
        Assert.assertEquals(stockInfo.getProfitLossStatus(), "BREAK_EVEN", "Status should be BREAK_EVEN");
        
        // Test with higher purchase price (loss scenario)
        stockInfo.setPurchasePrice(150.0);
        Assert.assertTrue(stockInfo.getProfitLoss() < 0, "Should show loss when purchase price is higher");
        Assert.assertTrue(stockInfo.isLoss(), "Should be in loss");
        
        // Test with lower purchase price (profit scenario)
        stockInfo.setPurchasePrice(50.0);
        Assert.assertTrue(stockInfo.getProfitLoss() > 0, "Should show profit when purchase price is lower");
        Assert.assertTrue(stockInfo.isProfit(), "Should be in profit");
        
        logger.info("Profit/loss edge cases test completed");
    }
    
    /**
     * Data provider for stocks with purchase prices
     * @return Array of StockInfo objects with purchase prices
     */
    @DataProvider(name = "stocksWithPurchasePrice")
    public Object[][] stocksWithPurchasePriceProvider() {
        testDataReader = TestDataReader.getInstance();
        List<StockInfo> stocks = testDataReader.getStocksForParallelTesting(5); // Test 5 stocks
        
        Object[][] data = new Object[stocks.size()][1];
        for (int i = 0; i < stocks.size(); i++) {
            data[i][0] = stocks.get(i);
        }
        
        return data;
    }
    
    /**
     * Data provider for profit scenarios (stocks likely to be in profit)
     * @return Array of StockInfo objects with lower purchase prices
     */
    @DataProvider(name = "profitScenarios")
    public Object[][] profitScenariosProvider() {
        testDataReader = TestDataReader.getInstance();
        List<StockInfo> stocks = testDataReader.getStocksForParallelTesting(3);
        
        // Modify purchase prices to create profit scenarios (set lower prices)
        for (StockInfo stock : stocks) {
            stock.setPurchasePrice(stock.getPurchasePrice() * 0.8); // 20% lower
        }
        
        Object[][] data = new Object[stocks.size()][1];
        for (int i = 0; i < stocks.size(); i++) {
            data[i][0] = stocks.get(i);
        }
        
        return data;
    }
    
    /**
     * Data provider for loss scenarios (stocks likely to be in loss)
     * @return Array of StockInfo objects with higher purchase prices
     */
    @DataProvider(name = "lossScenarios")
    public Object[][] lossScenariosProvider() {
        testDataReader = TestDataReader.getInstance();
        List<StockInfo> stocks = testDataReader.getStocksForParallelTesting(3);
        
        // Modify purchase prices to create loss scenarios (set higher prices)
        for (StockInfo stock : stocks) {
            stock.setPurchasePrice(stock.getPurchasePrice() * 1.2); // 20% higher
        }
        
        Object[][] data = new Object[stocks.size()][1];
        for (int i = 0; i < stocks.size(); i++) {
            data[i][0] = stocks.get(i);
        }
        
        return data;
    }
}
