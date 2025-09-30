package com.nse.stock.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Enhanced Driver Manager class with comprehensive Edge browser support
 * Compatible with Selenium 3.x and handles WebDriverManager network issues
 * Supports Chrome, Firefox, and Edge browsers with parallel execution
 */
public class DriverManager {
    
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigReader configReader = ConfigReader.getInstance();
    
    /**
     * Get WebDriver instance for the specified browser
     * @param browserName Browser name (chrome, firefox, edge)
     * @return WebDriver instance
     */
    public static WebDriver getDriver(String browserName) {
        if (driverThreadLocal.get() == null) {
            driverThreadLocal.set(createDriver(browserName));
        }
        return driverThreadLocal.get();
    }
    
    /**
     * Get current WebDriver instance
     * @return Current WebDriver instance
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    /**
     * Create WebDriver instance based on browser name
     * @param browserName Browser name
     * @return WebDriver instance
     */
    private static WebDriver createDriver(String browserName) {
        WebDriver driver;
        String browser = browserName != null ? browserName.toLowerCase() : 
                        configReader.getBrowser().toLowerCase();
        
        logger.info("Creating WebDriver for browser: {}", browser);
        
        switch (browser) {
            case "chrome":
                driver = createChromeDriver();
                break;
            case "firefox":
                driver = createFirefoxDriver();
                break;
            case "edge":
                driver = createEdgeDriverWithFallback();
                break;
            default:
                logger.warn("Unknown browser: {}. Defaulting to Chrome", browser);
                driver = createChromeDriver();
        }
        
        logger.info("WebDriver created successfully for browser: {}", browser);
        return driver;
    }
    
    /**
     * Create Chrome WebDriver with options
     * @return Chrome WebDriver instance
     */
    private static WebDriver createChromeDriver() {
        try {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-extensions");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            options.addArguments("--disable-features=VizDisplayCompositor");

            if (configReader.isHeadless()) {
                options.addArguments("--headless");
            }

            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            logger.info("Creating Chrome WebDriver with latest driver version");
            return new ChromeDriver(options);
        } catch (Exception e) {
            logger.error("Failed to create Chrome driver: {}", e.getMessage());
            throw new RuntimeException("Chrome driver setup failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create Firefox WebDriver with options
     * @return Firefox WebDriver instance
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        if (configReader.isHeadless()) {
            options.addArguments("--headless");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        options.addPreference("dom.push.enabled", false);
        options.addPreference("dom.webnotifications.enabled", false);
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        logger.info("Creating Firefox WebDriver");
        return new FirefoxDriver(options);
    }
    
    /**
     * Create Edge WebDriver with comprehensive fallback mechanism
     * Handles network connectivity issues to Microsoft EdgeDriver servers
     * @return Edge WebDriver instance
     */
    private static WebDriver createEdgeDriverWithFallback() {
        logger.info("Starting Edge WebDriver setup with enhanced fallback mechanism...");

        // Method 1: Try WebDriverManager with timeout
        try {
            logger.info("Method 1: Attempting WebDriverManager setup with timeout...");
            WebDriverManager edgeManager = WebDriverManager.edgedriver();
            edgeManager.timeout(15); // 15 second timeout for faster fallback
            edgeManager.setup();
            logger.info("WebDriverManager setup successful for EdgeDriver");
        } catch (Exception e) {
            logger.warn("WebDriverManager failed: {}. Trying fallback methods...", e.getMessage());

            // Method 2: Try to find and use local EdgeDriver
            try {
                logger.info("Method 2: Searching for local EdgeDriver installation...");
                String edgeDriverPath = findLocalEdgeDriver();
                if (edgeDriverPath != null) {
                    System.setProperty("webdriver.edge.driver", edgeDriverPath);
                    logger.info("Using local EdgeDriver at: {}", edgeDriverPath);
                } else {
                    logger.warn("No local EdgeDriver found. Proceeding with system PATH...");
                }
            } catch (Exception fallbackException) {
                logger.warn("Local EdgeDriver search failed: {}. Proceeding anyway...", fallbackException.getMessage());
            }
        }

        // Create EdgeOptions with basic settings (Selenium 3.x compatible)
        EdgeOptions options = new EdgeOptions();

        if (configReader.isHeadless()) {
            // For Selenium 3.x, use capability setting
            options.setCapability("ms:edgeOptions", java.util.Collections.singletonMap("args", 
                java.util.Arrays.asList("--headless", "--no-sandbox", "--disable-gpu")));
        } else {
            // Basic options for non-headless mode
            options.setCapability("ms:edgeOptions", java.util.Collections.singletonMap("args", 
                java.util.Arrays.asList("--no-sandbox", "--disable-gpu", "--remote-allow-origins=*")));
        }

        try {
            logger.info("Creating Edge WebDriver with basic options...");
            return new EdgeDriver(options);
        } catch (Exception e) {
            logger.error("Failed to create Edge driver: {}", e.getMessage());
            throw new RuntimeException("Edge driver setup failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find local EdgeDriver executable in common installation paths
     * @return Path to EdgeDriver executable or null if not found
     */
    private static String findLocalEdgeDriver() {
        String[] possiblePaths = {
            // Project-specific driver location
            "drivers/msedgedriver.exe",
            "./drivers/msedgedriver.exe",
            
            // Standard Microsoft Edge installation paths
            "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedgedriver.exe",
            "C:\\Program Files\\Microsoft\\Edge\\Application\\msedgedriver.exe",
            
            // User-specific paths
            System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\Edge\\Application\\msedgedriver.exe",
            
            // WebDriverManager cache paths
            System.getProperty("user.home") + "\\.cache\\selenium\\msedgedriver.exe",
            System.getProperty("user.home") + "\\.wdm\\drivers\\edgedriver\\msedgedriver.exe",
            
            // Try PATH environment variable
            "msedgedriver.exe"
        };
        
        for (String path : possiblePaths) {
            try {
                java.io.File file = new java.io.File(path);
                if (file.exists() && file.canExecute()) {
                    logger.info("Found EdgeDriver at: {}", path);
                    return file.getAbsolutePath();
                }
            } catch (Exception e) {
                logger.debug("Could not access EdgeDriver at: {}", path);
            }
        }

        logger.warn("No local EdgeDriver found in standard locations");
        return null;
    }
    
    /**
     * Quit the current WebDriver instance with proper cleanup
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                logger.info("Quitting WebDriver");
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.warn("Error while quitting WebDriver: {}", e.getMessage());
                killBrowserProcesses();
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Force kill browser processes if they don't close properly
     */
    private static void killBrowserProcesses() {
        try {
            Runtime.getRuntime().exec("taskkill /F /IM chrome.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM firefox.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM msedge.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM msedgedriver.exe /T");
            logger.info("Browser processes killed forcefully");
        } catch (Exception e) {
            logger.warn("Error killing browser processes: {}", e.getMessage());
        }
    }
}
