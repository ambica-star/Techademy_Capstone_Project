package com.nse.stock.listeners;

import com.nse.stock.utils.ConfigReader;
import com.nse.stock.utils.DriverManager;
import com.nse.stock.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Screenshot listener for capturing screenshots on test events
 * Implements TestNG ITestListener to capture screenshots on failure/success
 */
public class ScreenshotListener implements ITestListener {
    
    private static final Logger logger = LogManager.getLogger(ScreenshotListener.class);
    private static final ConfigReader configReader = ConfigReader.getInstance();
    
    /**
     * Called after a test method fails
     */
    @Override
    public void onTestFailure(ITestResult result) {
        if (configReader.isScreenshotOnFailure()) {
            captureScreenshot(result, "FAILED");
        }
    }
    
    /**
     * Called after a test method succeeds
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        if (configReader.isScreenshotOnPass()) {
            captureScreenshot(result, "PASSED");
        }
    }
    
    /**
     * Called after a test method is skipped
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        // Capture screenshot for skipped tests as well
        captureScreenshot(result, "SKIPPED");
    }
    
    /**
     * Capture screenshot for the given test result
     * @param result Test result
     * @param status Test status (PASSED/FAILED/SKIPPED)
     */
    private void captureScreenshot(ITestResult result, String status) {
        try {
            if (DriverManager.getDriver() != null) {
                String testName = result.getMethod().getMethodName();
                String className = result.getTestClass().getName();
                String browser = System.getProperty("browser", "unknown");
                
                // Create descriptive screenshot name
                String screenshotName = String.format("%s_%s_%s_%s", 
                    status, className.substring(className.lastIndexOf('.') + 1), testName, browser);
                
                // Capture screenshot
                String screenshotPath = ScreenshotUtils.takeScreenshot(DriverManager.getDriver(), screenshotName);
                
                if (screenshotPath != null) {
                    logger.info("Screenshot captured for {} test: {} -> {}", status, testName, screenshotPath);
                    
                    // Store screenshot path in test result for potential use by other listeners
                    System.setProperty("screenshot.path." + testName, screenshotPath);
                } else {
                    logger.warn("Failed to capture screenshot for {} test: {}", status, testName);
                }
            } else {
                logger.warn("WebDriver is null, cannot capture screenshot for test: {}", 
                           result.getMethod().getMethodName());
            }
        } catch (Exception e) {
            logger.error("Error capturing screenshot for test {}: {}", 
                        result.getMethod().getMethodName(), e.getMessage(), e);
        }
    }
    
    /**
     * Called before any test method is invoked
     */
    @Override
    public void onTestStart(ITestResult result) {
        // Log test start with browser information
        String testName = result.getMethod().getMethodName();
        String browser = System.getProperty("browser", "unknown");
        String threadName = Thread.currentThread().getName();
        
        logger.info("Test started: {} on browser: {} in thread: {}", testName, browser, threadName);
    }
}
