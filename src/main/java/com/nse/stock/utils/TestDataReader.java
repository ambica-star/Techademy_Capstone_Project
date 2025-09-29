package com.nse.stock.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nse.stock.models.StockInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to read test data from JSON files
 * Provides methods to load stock data and test scenarios
 */
public class TestDataReader {
    
    private static final Logger logger = LogManager.getLogger(TestDataReader.class);
    private static TestDataReader instance;
    private ObjectMapper objectMapper;
    private JsonNode stocksData;
    
    private TestDataReader() {
        objectMapper = new ObjectMapper();
        loadStockData();
    }
    
    /**
     * Get singleton instance of TestDataReader
     * @return TestDataReader instance
     */
    public static TestDataReader getInstance() {
        if (instance == null) {
            synchronized (TestDataReader.class) {
                if (instance == null) {
                    instance = new TestDataReader();
                }
            }
        }
        return instance;
    }
    
    /**
     * Load stock data from JSON file
     */
    private void loadStockData() {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("testdata/stocks.json")) {
            
            if (inputStream != null) {
                stocksData = objectMapper.readTree(inputStream);
                logger.info("Stock test data loaded successfully");
            } else {
                logger.error("stocks.json file not found in classpath");
                throw new RuntimeException("stocks.json file not found");
            }
        } catch (IOException e) {
            logger.error("Error loading stock test data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load stock test data", e);
        }
    }
    
    /**
     * Get all NIFTY 50 stocks from test data
     * @return List of StockInfo objects
     */
    public List<StockInfo> getNifty50Stocks() {
        List<StockInfo> stocks = new ArrayList<>();
        
        try {
            JsonNode nifty50Stocks = stocksData.get("nifty50_stocks");
            
            if (nifty50Stocks != null && nifty50Stocks.isArray()) {
                for (JsonNode stockNode : nifty50Stocks) {
                    StockInfo stock = new StockInfo();
                    stock.setSymbol(stockNode.get("symbol").asText());
                    stock.setCompanyName(stockNode.get("companyName").asText());
                    stock.setPurchasePrice(stockNode.get("purchasePrice").asDouble());
                    
                    stocks.add(stock);
                }
                logger.info("Loaded {} NIFTY 50 stocks from test data", stocks.size());
            }
        } catch (Exception e) {
            logger.error("Error parsing NIFTY 50 stocks: {}", e.getMessage(), e);
        }
        
        return stocks;
    }
    
    /**
     * Get stock information by symbol
     * @param symbol Stock symbol
     * @return StockInfo object or null if not found
     */
    public StockInfo getStockBySymbol(String symbol) {
        try {
            JsonNode nifty50Stocks = stocksData.get("nifty50_stocks");
            
            if (nifty50Stocks != null && nifty50Stocks.isArray()) {
                for (JsonNode stockNode : nifty50Stocks) {
                    if (symbol.equalsIgnoreCase(stockNode.get("symbol").asText())) {
                        StockInfo stock = new StockInfo();
                        stock.setSymbol(stockNode.get("symbol").asText());
                        stock.setCompanyName(stockNode.get("companyName").asText());
                        stock.setPurchasePrice(stockNode.get("purchasePrice").asDouble());
                        
                        logger.debug("Found stock data for symbol: {}", symbol);
                        return stock;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error finding stock by symbol {}: {}", symbol, e.getMessage(), e);
        }
        
        logger.warn("Stock not found for symbol: {}", symbol);
        return null;
    }
    
    /**
     * Get list of stock symbols for testing
     * @return Array of stock symbols
     */
    public String[] getStockSymbols() {
        List<String> symbols = new ArrayList<>();
        
        try {
            JsonNode nifty50Stocks = stocksData.get("nifty50_stocks");
            
            if (nifty50Stocks != null && nifty50Stocks.isArray()) {
                for (JsonNode stockNode : nifty50Stocks) {
                    symbols.add(stockNode.get("symbol").asText());
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting stock symbols: {}", e.getMessage(), e);
        }
        
        return symbols.toArray(new String[0]);
    }
    
    /**
     * Get test scenarios from JSON data
     * @return List of test scenario descriptions
     */
    public List<String> getTestScenarios() {
        List<String> scenarios = new ArrayList<>();
        
        try {
            JsonNode testScenarios = stocksData.get("test_scenarios");
            
            if (testScenarios != null && testScenarios.isArray()) {
                for (JsonNode scenarioNode : testScenarios) {
                    String testName = scenarioNode.get("testName").asText();
                    String description = scenarioNode.get("description").asText();
                    scenarios.add(testName + ": " + description);
                }
                logger.info("Loaded {} test scenarios", scenarios.size());
            }
        } catch (Exception e) {
            logger.error("Error loading test scenarios: {}", e.getMessage(), e);
        }
        
        return scenarios;
    }
    
    /**
     * Get random stock for testing
     * @return Random StockInfo object
     */
    public StockInfo getRandomStock() {
        List<StockInfo> stocks = getNifty50Stocks();
        if (!stocks.isEmpty()) {
            int randomIndex = (int) (Math.random() * stocks.size());
            StockInfo randomStock = stocks.get(randomIndex);
            logger.debug("Selected random stock: {}", randomStock.getSymbol());
            return randomStock;
        }
        return null;
    }
    
    /**
     * Get stocks for parallel testing (subset of all stocks)
     * @param count Number of stocks to return
     * @return List of StockInfo objects
     */
    public List<StockInfo> getStocksForParallelTesting(int count) {
        List<StockInfo> allStocks = getNifty50Stocks();
        List<StockInfo> selectedStocks = new ArrayList<>();
        
        int actualCount = Math.min(count, allStocks.size());
        for (int i = 0; i < actualCount; i++) {
            selectedStocks.add(allStocks.get(i));
        }
        
        logger.info("Selected {} stocks for parallel testing", selectedStocks.size());
        return selectedStocks;
    }
    
    /**
     * Validate if test data is properly loaded
     * @return true if data is valid
     */
    public boolean isTestDataValid() {
        try {
            return stocksData != null && 
                   stocksData.has("nifty50_stocks") && 
                   stocksData.get("nifty50_stocks").isArray() &&
                   stocksData.get("nifty50_stocks").size() > 0;
        } catch (Exception e) {
            logger.error("Error validating test data: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get purchase price for a stock symbol
     * @param symbol Stock symbol
     * @return Purchase price or 0.0 if not found
     */
    public double getPurchasePrice(String symbol) {
        StockInfo stock = getStockBySymbol(symbol);
        return stock != null ? stock.getPurchasePrice() : 0.0;
    }
    
    /**
     * Get company name for a stock symbol
     * @param symbol Stock symbol
     * @return Company name or "Unknown" if not found
     */
    public String getCompanyName(String symbol) {
        StockInfo stock = getStockBySymbol(symbol);
        return stock != null ? stock.getCompanyName() : "Unknown";
    }
}
