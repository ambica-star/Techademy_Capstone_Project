package com.nse.stock.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.nse.stock.utils.ConfigReader;
import com.nse.stock.utils.DriverManager;
import com.nse.stock.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;

/**
 * ExtentReports listener for generating detailed test reports
 * Implements TestNG ITestListener to capture test events
 */
public class ExtentReportListener implements ITestListener {
    
    private static final Logger logger = LogManager.getLogger(ExtentReportListener.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final ConfigReader configReader = ConfigReader.getInstance();
    
    /**
     * Initialize ExtentReports before test suite starts
     */
    public static void initializeExtentReports() {
        if (extentReports == null) {
            String reportPath = configReader.getExtentReportPath();

            // Create reports directory if it doesn't exist
            File reportFile = new File(reportPath);
            File parentDir = reportFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                logger.info("Created reports directory: {}", parentDir.getAbsolutePath());
            }

            // Initialize ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            
            // Configure reporter
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle(configReader.getExtentReportTitle());
            sparkReporter.config().setReportName(configReader.getExtentReportName());
            sparkReporter.config().setTimeStampFormat("dd-MM-yyyy HH:mm:ss");
            
            // Initialize ExtentReports
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            
            // Set system information
            extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("User", System.getProperty("user.name"));
            extentReports.setSystemInfo("Environment", "NSE Stock Testing");
            extentReports.setSystemInfo("Browser", configReader.getBrowser());
            
            logger.info("ExtentReports initialized successfully");
        }
    }
    
    /**
     * Called before any test method is invoked
     */
    @Override
    public void onTestStart(ITestResult result) {
        // Initialize ExtentReports if not already done
        if (extentReports == null) {
            initializeExtentReports();
        }
        
        // Create test entry in report
        String testName = result.getMethod().getMethodName();
        String testDescription = result.getMethod().getDescription();
        String className = result.getTestClass().getName();
        
        ExtentTest test = extentReports.createTest(testName, testDescription);
        test.assignCategory(className);
        
        // Add browser information if available
        String browser = System.getProperty("browser");
        if (browser != null) {
            test.assignCategory("Browser: " + browser);
        }
        
        // Add thread information for parallel execution
        test.assignCategory("Thread: " + Thread.currentThread().getName());
        
        extentTest.set(test);
        
        logger.info("Test started: {} - {}", testName, testDescription);
    }
    
    /**
     * Called after a test method succeeds
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.PASS, "Test passed successfully");
            
            // Add screenshot if configured
            if (configReader.isScreenshotOnPass()) {
                addScreenshotToReport(test, "Test Passed");
            }
            
            // Add execution time
            long duration = result.getEndMillis() - result.getStartMillis();
            test.info("Execution time: " + duration + " ms");
        }
        
        logger.info("Test passed: {}", result.getMethod().getMethodName());
    }
    
    /**
     * Called after a test method fails
     */
    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            // Log failure details
            test.log(Status.FAIL, "Test failed");
            test.log(Status.FAIL, result.getThrowable());
            
            // Add screenshot for failure
            addScreenshotToReport(test, "Test Failed");
            
            // Add execution time
            long duration = result.getEndMillis() - result.getStartMillis();
            test.info("Execution time: " + duration + " ms");
        }
        
        logger.error("Test failed: {} - {}", result.getMethod().getMethodName(), 
                    result.getThrowable().getMessage());
    }
    
    /**
     * Called after a test method is skipped
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.SKIP, "Test skipped");
            test.log(Status.SKIP, result.getThrowable());
        }
        
        logger.warn("Test skipped: {} - {}", result.getMethod().getMethodName(), 
                   result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown reason");
    }
    
    /**
     * Called after all tests have run and all their Configuration methods have been called
     */
    @Override
    public void onFinish(org.testng.ITestContext context) {
        if (extentReports != null) {
            // Add test suite information
            extentReports.setSystemInfo("Total Tests", String.valueOf(context.getAllTestMethods().length));
            extentReports.setSystemInfo("Passed Tests", String.valueOf(context.getPassedTests().size()));
            extentReports.setSystemInfo("Failed Tests", String.valueOf(context.getFailedTests().size()));
            extentReports.setSystemInfo("Skipped Tests", String.valueOf(context.getSkippedTests().size()));
            
            // Calculate execution time
            long startTime = context.getStartDate().getTime();
            long endTime = context.getEndDate().getTime();
            long duration = endTime - startTime;
            extentReports.setSystemInfo("Total Execution Time", duration + " ms");
            
            // Flush the report
            extentReports.flush();
            
            logger.info("ExtentReports flushed successfully");
            logger.info("Report location: {}", configReader.getExtentReportPath());
        }
    }

    /**
     * Force flush ExtentReports (for manual report generation)
     */
    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports manually flushed");
        }
    }

    /**
     * Get ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        if (extentReports == null) {
            initializeExtentReports();
        }
        return extentReports;
    }
    
    /**
     * Add screenshot to ExtentReports
     * @param test ExtentTest instance
     * @param screenshotName Name for the screenshot
     */
    private void addScreenshotToReport(ExtentTest test, String screenshotName) {
        try {
            if (DriverManager.getDriver() != null) {
                // Capture screenshot as base64
                String base64Screenshot = ScreenshotUtils.getScreenshotAsBase64(DriverManager.getDriver());
                
                if (base64Screenshot != null) {
                    test.addScreenCaptureFromBase64String(base64Screenshot, screenshotName);
                    logger.debug("Screenshot added to report: {}", screenshotName);
                } else {
                    // Fallback: try to capture and save screenshot file
                    String screenshotPath = ScreenshotUtils.takeScreenshot(DriverManager.getDriver(), screenshotName);
                    if (screenshotPath != null) {
                        test.addScreenCaptureFromPath(screenshotPath, screenshotName);
                        logger.debug("Screenshot file added to report: {}", screenshotPath);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to add screenshot to report: {}", e.getMessage());
            test.warning("Screenshot capture failed: " + e.getMessage());
        }
    }
    
    /**
     * Get current ExtentTest instance
     * @return Current ExtentTest
     */
    public static ExtentTest getCurrentTest() {
        return extentTest.get();
    }
    
    /**
     * Log info message to current test
     * @param message Message to log
     */
    public static void logInfo(String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.info(message);
        }
    }
    
    /**
     * Log warning message to current test
     * @param message Message to log
     */
    public static void logWarning(String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.warning(message);
        }
    }
    
    /**
     * Log error message to current test
     * @param message Message to log
     */
    public static void logError(String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.fail(message);
        }
    }
    
    /**
     * Add custom information to current test
     * @param key Information key
     * @param value Information value
     */
    public static void addTestInfo(String key, String value) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.info(key + ": " + value);
        }
    }
}
