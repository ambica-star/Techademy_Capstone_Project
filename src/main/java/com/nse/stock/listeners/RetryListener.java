package com.nse.stock.listeners;

import com.nse.stock.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry analyzer for handling flaky tests
 * Implements TestNG IRetryAnalyzer to retry failed tests
 */
public class RetryListener implements IRetryAnalyzer {
    
    private static final Logger logger = LogManager.getLogger(RetryListener.class);
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private int retryCount = 0;
    private final int maxRetryCount;
    
    public RetryListener() {
        this.maxRetryCount = configReader.getMaxRetryCount();
    }
    
    /**
     * Determines whether a test should be retried
     * @param result Test result
     * @return true if test should be retried, false otherwise
     */
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            
            String testName = result.getMethod().getMethodName();
            String failureReason = result.getThrowable() != null ? 
                result.getThrowable().getMessage() : "Unknown failure";
            
            logger.warn("Test failed: {} - Attempt {} of {}. Reason: {}", 
                       testName, retryCount, maxRetryCount, failureReason);
            
            // Add retry information to test result
            result.setAttribute("retryCount", retryCount);
            result.setAttribute("maxRetryCount", maxRetryCount);
            
            // Log retry attempt
            logger.info("Retrying test: {} (Attempt {} of {})", testName, retryCount, maxRetryCount);
            
            return true;
        }
        
        // Max retries reached
        String testName = result.getMethod().getMethodName();
        logger.error("Test failed after {} attempts: {}", maxRetryCount, testName);
        
        return false;
    }
    
    /**
     * Get current retry count
     * @return Current retry count
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /**
     * Get maximum retry count
     * @return Maximum retry count
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }
    
    /**
     * Reset retry count (useful for new test execution)
     */
    public void resetRetryCount() {
        this.retryCount = 0;
    }
}
