package com.nse.stock.base;

import com.nse.stock.utils.ConfigReader;
import com.nse.stock.utils.DriverManager;
import com.nse.stock.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Base Test class that provides common setup and teardown functionality
 * for all test classes in the NSE Stock Testing framework.
 */
public class BaseTest {
    
    private static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected ConfigReader configReader;
    
    @BeforeClass
    @Parameters({"browser"})
    public void setUpClass(@Optional("chrome") String browser) {
        logger.info("Setting up test class with browser: {}", browser);
        configReader = ConfigReader.getInstance();
        
        // Override browser from parameter if provided
        if (browser != null && !browser.isEmpty()) {
            System.setProperty("browser", browser);
        }
    }
    
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        logger.info("Starting test setup for browser: {}", browser);
        
        try {
            // Initialize WebDriver
            driver = DriverManager.getDriver(browser);
            
            // Configure timeouts for Selenium 3.x
            driver.manage().timeouts().implicitlyWait(
                configReader.getImplicitWait(), TimeUnit.SECONDS
            );
            driver.manage().timeouts().pageLoadTimeout(
                configReader.getPageLoadTimeout(), TimeUnit.SECONDS
            );
            
            // Maximize window
            driver.manage().window().maximize();
            
            logger.info("WebDriver setup completed successfully");
            
        } catch (Exception e) {
            logger.error("Failed to setup WebDriver: {}", e.getMessage(), e);
            throw new RuntimeException("WebDriver setup failed", e);
        }
    }
    
    @AfterMethod
    public void tearDown() {
        logger.info("Starting test teardown");
        
        try {
            if (driver != null) {
                // Take screenshot before closing (optional)
                if (configReader.isScreenshotOnPass()) {
                    ScreenshotUtils.takeScreenshot(driver, "test_completed");
                }
                
                // Close browser
                DriverManager.quitDriver();
                logger.info("WebDriver closed successfully");
            }
        } catch (Exception e) {
            logger.error("Error during teardown: {}", e.getMessage(), e);
        }
    }
    
    @AfterClass
    public void tearDownClass() {
        logger.info("Test class teardown completed");
    }
    
    /**
     * Get the current WebDriver instance
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        return driver;
    }
    
    /**
     * Navigate to NSE India website
     */
    protected void navigateToNSE() {
        String baseUrl = configReader.getNSEBaseUrl();
        logger.info("Navigating to NSE website: {}", baseUrl);
        driver.get(baseUrl);
        
        // Wait for page to load
        try {
            Thread.sleep(3000); // Allow time for page to fully load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Thread interrupted while waiting for page load");
        }
    }
    
    /**
     * Take screenshot with custom name
     * @param screenshotName Name for the screenshot
     */
    protected void takeScreenshot(String screenshotName) {
        ScreenshotUtils.takeScreenshot(driver, screenshotName);
    }
}
