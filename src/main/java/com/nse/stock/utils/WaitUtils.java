package com.nse.stock.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;
import java.util.List;

/**
 * Utility class for WebDriver wait operations
 * Provides common wait methods for better test stability
 */
public class WaitUtils {
    
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    private static final ConfigReader configReader = ConfigReader.getInstance();
    
    /**
     * Wait for element to be clickable
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return WebElement if found and clickable
     */
    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator) {
        return waitForElementToBeClickable(driver, locator, configReader.getExplicitWait());
    }
    
    /**
     * Wait for element to be clickable with custom timeout
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param timeoutSeconds Timeout in seconds
     * @return WebElement if found and clickable
     */
    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Element found and clickable: {}", locator);
            return element;
        } catch (Exception e) {
            logger.error("Element not clickable within {} seconds: {}", timeoutSeconds, locator);
            throw e;
        }
    }
    
    /**
     * Wait for element to be visible
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return WebElement if found and visible
     */
    public static WebElement waitForElementToBeVisible(WebDriver driver, By locator) {
        return waitForElementToBeVisible(driver, locator, configReader.getExplicitWait());
    }
    
    /**
     * Wait for element to be visible with custom timeout
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param timeoutSeconds Timeout in seconds
     * @return WebElement if found and visible
     */
    public static WebElement waitForElementToBeVisible(WebDriver driver, By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("Element found and visible: {}", locator);
            return element;
        } catch (Exception e) {
            logger.error("Element not visible within {} seconds: {}", timeoutSeconds, locator);
            throw e;
        }
    }
    
    /**
     * Wait for element to be present in DOM
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return WebElement if found
     */
    public static WebElement waitForElementToBePresent(WebDriver driver, By locator) {
        return waitForElementToBePresent(driver, locator, configReader.getExplicitWait());
    }
    
    /**
     * Wait for element to be present in DOM with custom timeout
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param timeoutSeconds Timeout in seconds
     * @return WebElement if found
     */
    public static WebElement waitForElementToBePresent(WebDriver driver, By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            logger.debug("Element found in DOM: {}", locator);
            return element;
        } catch (Exception e) {
            logger.error("Element not present within {} seconds: {}", timeoutSeconds, locator);
            throw e;
        }
    }
    
    /**
     * Wait for elements to be present
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return List of WebElements if found
     */
    public static List<WebElement> waitForElementsToBePresent(WebDriver driver, By locator) {
        return waitForElementsToBePresent(driver, locator, configReader.getExplicitWait());
    }
    
    /**
     * Wait for elements to be present with custom timeout
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param timeoutSeconds Timeout in seconds
     * @return List of WebElements if found
     */
    public static List<WebElement> waitForElementsToBePresent(WebDriver driver, By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
            List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            logger.debug("Elements found in DOM: {} (count: {})", locator, elements.size());
            return elements;
        } catch (Exception e) {
            logger.error("Elements not present within {} seconds: {}", timeoutSeconds, locator);
            throw e;
        }
    }
    
    /**
     * Wait for text to be present in element
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param text Text to wait for
     * @return true if text is present
     */
    public static boolean waitForTextToBePresentInElement(WebDriver driver, By locator, String text) {
        return waitForTextToBePresentInElement(driver, locator, text, configReader.getExplicitWait());
    }
    
    /**
     * Wait for text to be present in element with custom timeout
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param text Text to wait for
     * @param timeoutSeconds Timeout in seconds
     * @return true if text is present
     */
    public static boolean waitForTextToBePresentInElement(WebDriver driver, By locator, String text, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
            boolean result = wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            logger.debug("Text '{}' found in element: {}", text, locator);
            return result;
        } catch (Exception e) {
            logger.error("Text '{}' not found in element within {} seconds: {}", text, timeoutSeconds, locator);
            return false;
        }
    }
    
    /**
     * Wait for element to disappear
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return true if element is no longer visible
     */
    public static boolean waitForElementToDisappear(WebDriver driver, By locator) {
        return waitForElementToDisappear(driver, locator, configReader.getExplicitWait());
    }
    
    /**
     * Wait for element to disappear with custom timeout
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param timeoutSeconds Timeout in seconds
     * @return true if element is no longer visible
     */
    public static boolean waitForElementToDisappear(WebDriver driver, By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
            boolean result = wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.debug("Element disappeared: {}", locator);
            return result;
        } catch (Exception e) {
            logger.error("Element still visible after {} seconds: {}", timeoutSeconds, locator);
            return false;
        }
    }
    
    /**
     * Wait for page title to contain text
     * @param driver WebDriver instance
     * @param title Title text to wait for
     * @return true if title contains text
     */
    public static boolean waitForTitleToContain(WebDriver driver, String title) {
        return waitForTitleToContain(driver, title, configReader.getExplicitWait());
    }
    
    /**
     * Wait for page title to contain text with custom timeout
     * @param driver WebDriver instance
     * @param title Title text to wait for
     * @param timeoutSeconds Timeout in seconds
     * @return true if title contains text
     */
    public static boolean waitForTitleToContain(WebDriver driver, String title, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
            boolean result = wait.until(ExpectedConditions.titleContains(title));
            logger.debug("Page title contains: {}", title);
            return result;
        } catch (Exception e) {
            logger.error("Page title does not contain '{}' within {} seconds", title, timeoutSeconds);
            return false;
        }
    }
    
    /**
     * Wait for URL to contain text
     * @param driver WebDriver instance
     * @param urlPart URL part to wait for
     * @return true if URL contains text
     */
    public static boolean waitForUrlToContain(WebDriver driver, String urlPart) {
        return waitForUrlToContain(driver, urlPart, configReader.getExplicitWait());
    }
    
    /**
     * Wait for URL to contain text with custom timeout
     * @param driver WebDriver instance
     * @param urlPart URL part to wait for
     * @param timeoutSeconds Timeout in seconds
     * @return true if URL contains text
     */
    public static boolean waitForUrlToContain(WebDriver driver, String urlPart, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
            boolean result = wait.until(ExpectedConditions.urlContains(urlPart));
            logger.debug("URL contains: {}", urlPart);
            return result;
        } catch (Exception e) {
            logger.error("URL does not contain '{}' within {} seconds", urlPart, timeoutSeconds);
            return false;
        }
    }
    
    /**
     * Custom wait with polling
     * @param driver WebDriver instance
     * @param timeoutSeconds Timeout in seconds
     * @param pollingIntervalSeconds Polling interval in seconds
     */
    public static void customWait(WebDriver driver, int timeoutSeconds, int pollingIntervalSeconds) {
        try {
            Thread.sleep(pollingIntervalSeconds * 1000L);
            logger.debug("Custom wait completed: {} seconds", pollingIntervalSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Custom wait interrupted");
        }
    }
    
    /**
     * Simple sleep wait (use sparingly)
     * @param seconds Seconds to wait
     */
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.debug("Sleep wait completed: {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep wait interrupted");
        }
    }
}
