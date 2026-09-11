package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for reading configuration properties from config.properties.
 *
 * All test configuration (base URL, credentials, browser settings) is centralized
 * in src/main/resources/config.properties so tests never have hardcoded values.
 *
 * Available properties:
 *   baseUrlResonance        — API base URL (e.g., https://resonance.dibimbing.id)
 *   usernameOrEmailResonance — login username or email
 *   passwordResonance       — login password
 *   webUrl                  — browser base URL for Selenium tests
 *   browser                 — browser type: chrome, firefox, edge
 *   headless                — true/false for headless browser mode (used in CI)
 */
public class ConfigReader {

    /** Loaded once at class initialization; shared across all tests. */
    private static final Properties properties = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage());
        }
    }

    /**
     * Returns the value of a property by key.
     *
     * @param key the property name (e.g., "baseUrlResonance")
     * @return the property value, or null if the key does not exist
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
