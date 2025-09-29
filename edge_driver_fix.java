    /**
     * Create Edge WebDriver with enhanced fallback mechanism
     * @return Edge WebDriver instance
     */
    private static WebDriver createEdgeDriver() {
        logger.info("Starting Edge WebDriver setup with enhanced fallback mechanism...");
        
        // Method 1: Try WebDriverManager with timeout
        try {
            logger.info("Method 1: Attempting WebDriverManager setup with timeout...");
            WebDriverManager edgeManager = WebDriverManager.edgedriver();
            edgeManager.timeout(15); // 15 second timeout for faster fallback
            edgeManager.setup();
            logger.info("✅ WebDriverManager setup successful for EdgeDriver");
        } catch (Exception e) {
            logger.warn("❌ WebDriverManager failed: {}. Trying fallback methods...", e.getMessage());
            
            // Method 2: Try to find system EdgeDriver
            try {
                logger.info("Method 2: Searching for system EdgeDriver...");
                String edgeDriverPath = findSystemEdgeDriver();
                if (edgeDriverPath != null) {
                    System.setProperty("webdriver.edge.driver", edgeDriverPath);
                    logger.info("✅ Using system EdgeDriver at: {}", edgeDriverPath);
                } else {
                    logger.warn("❌ No system EdgeDriver found. Proceeding without explicit driver setup...");
                }
            } catch (Exception fallbackException) {
                logger.warn("❌ System EdgeDriver search failed: {}. Proceeding anyway...", fallbackException.getMessage());
            }
        }

        EdgeOptions options = new EdgeOptions();

        // Enhanced Edge options for maximum compatibility
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

        // Exclude automation detection
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        try {
            logger.info("Creating Edge WebDriver with enhanced options...");
            return new EdgeDriver(options);
        } catch (Exception e) {
            logger.error("Failed to create Edge driver: {}", e.getMessage());
            throw new RuntimeException("Edge driver setup failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find system EdgeDriver executable
     * @return Path to EdgeDriver executable or null if not found
     */
    private static String findSystemEdgeDriver() {
        String[] possiblePaths = {
            // Standard installation paths
            "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedgedriver.exe",
            "C:\\Program Files\\Microsoft\\Edge\\Application\\msedgedriver.exe",
            
            // User-specific paths
            System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\Edge\\Application\\msedgedriver.exe",
            System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\EdgeWebView\\Application\\msedgedriver.exe",
            
            // WebDriver cache paths
            System.getProperty("user.home") + "\\.cache\\selenium\\msedgedriver.exe",
            System.getProperty("user.home") + "\\.wdm\\drivers\\edgedriver\\msedgedriver.exe",
            
            // Try PATH
            "msedgedriver.exe"
        };
        
        for (String path : possiblePaths) {
            try {
                java.io.File file = new java.io.File(path);
                if (file.exists() && file.canExecute()) {
                    logger.info("✅ Found EdgeDriver at: {}", path);
                    return path;
                }
            } catch (Exception e) {
                logger.debug("Could not access EdgeDriver at: {}", path);
            }
        }
        
        logger.warn("❌ No system EdgeDriver found in standard locations");
        return null;
    }
