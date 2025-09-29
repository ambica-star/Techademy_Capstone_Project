package com.nse.stock.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot utility class for capturing and managing screenshots
 * during test execution
 */
public class ScreenshotUtils {
    
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Take screenshot and save to configured directory
     * @param driver WebDriver instance
     * @param screenshotName Name for the screenshot file
     * @return Path to the saved screenshot
     */
    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        if (driver == null) {
            logger.warn("WebDriver is null, cannot take screenshot");
            return null;
        }
        
        try {
            // Create screenshots directory if it doesn't exist
            String screenshotDir = configReader.getScreenshotDirectory();
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generate timestamp
            String timestamp = LocalDateTime.now().format(formatter);
            
            // Create filename
            String fileName = String.format("%s_%s_%s.png", 
                screenshotName, 
                Thread.currentThread().getName(),
                timestamp);
            
            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            // Save screenshot
            String filePath = screenshotDir + File.separator + fileName;
            File destFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destFile);
            
            logger.info("Screenshot saved: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("Failed to take screenshot: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Take screenshot for test failure
     * @param driver WebDriver instance
     * @param testName Test method name
     * @return Path to the saved screenshot
     */
    public static String takeFailureScreenshot(WebDriver driver, String testName) {
        return takeScreenshot(driver, "FAILED_" + testName);
    }
    
    /**
     * Take screenshot for test pass
     * @param driver WebDriver instance
     * @param testName Test method name
     * @return Path to the saved screenshot
     */
    public static String takePassScreenshot(WebDriver driver, String testName) {
        return takeScreenshot(driver, "PASSED_" + testName);
    }
    
    /**
     * Take screenshot with custom prefix
     * @param driver WebDriver instance
     * @param prefix Screenshot prefix
     * @param testName Test method name
     * @return Path to the saved screenshot
     */
    public static String takeScreenshotWithPrefix(WebDriver driver, String prefix, String testName) {
        return takeScreenshot(driver, prefix + "_" + testName);
    }
    
    /**
     * Get screenshot as base64 string for embedding in reports
     * @param driver WebDriver instance
     * @return Base64 encoded screenshot string
     */
    public static String getScreenshotAsBase64(WebDriver driver) {
        if (driver == null) {
            logger.warn("WebDriver is null, cannot capture screenshot as base64");
            return null;
        }
        
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as base64: {}", e.getMessage(), e);
            return null;
        }
    }
}
