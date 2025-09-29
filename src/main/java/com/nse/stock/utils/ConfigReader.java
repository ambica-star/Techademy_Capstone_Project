package com.nse.stock.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Reader utility class to read properties from config.properties file
 * Implements Singleton pattern for efficient resource usage
 */
public class ConfigReader {
    
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private Properties properties;
    
    private ConfigReader() {
        loadProperties();
    }
    
    /**
     * Get singleton instance of ConfigReader
     * @return ConfigReader instance
     */
    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }
    
    /**
     * Load properties from config.properties file
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Configuration properties loaded successfully");
            } else {
                logger.error("config.properties file not found in classpath");
                throw new RuntimeException("config.properties file not found");
            }
        } catch (IOException e) {
            logger.error("Error loading configuration properties: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load configuration properties", e);
        }
    }
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        return value;
    }
    
    /**
     * Get property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    // Browser Configuration
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }
    
    // Timeout Configuration
    public int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }
    
    public int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "20"));
    }
    
    public int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout", "30"));
    }
    
    // URL Configuration
    public String getNSEBaseUrl() {
        return getProperty("nse.base.url", "https://www.nseindia.com/");
    }
    
    public String getNSEGetQuoteUrl() {
        return getProperty("nse.get.quote.url", "https://www.nseindia.com/get-quotes/equity");
    }
    
    // Test Data Configuration
    public String getDefaultStockSymbol() {
        return getProperty("default.stock.symbol", "TATAMOTORS");
    }
    
    public String[] getTestStocks() {
        String stocks = getProperty("test.stocks", "TATAMOTORS,RELIANCE,INFY");
        return stocks.split(",");
    }
    
    // Screenshot Configuration
    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }
    
    public boolean isScreenshotOnPass() {
        return Boolean.parseBoolean(getProperty("screenshot.on.pass", "false"));
    }
    
    public String getScreenshotDirectory() {
        return getProperty("screenshot.directory", "test-output/screenshots");
    }
    
    // Reporting Configuration
    public String getExtentReportPath() {
        return getProperty("extent.report.path", "test-output/reports/ExtentReport.html");
    }
    
    public String getExtentReportTitle() {
        return getProperty("extent.report.title", "NSE Stock Testing Report");
    }
    
    public String getExtentReportName() {
        return getProperty("extent.report.name", "Stock Information Verification Tests");
    }
    
    // Logging Configuration
    public String getLogLevel() {
        return getProperty("log.level", "INFO");
    }
    
    public String getLogFilePath() {
        return getProperty("log.file.path", "test-output/logs/automation.log");
    }
    
    // Parallel Execution Configuration
    public String[] getParallelBrowsers() {
        String browsers = getProperty("parallel.browsers", "chrome,firefox,edge");
        return browsers.split(",");
    }
    
    public int getMaxRetryCount() {
        return Integer.parseInt(getProperty("max.retry.count", "2"));
    }
}
