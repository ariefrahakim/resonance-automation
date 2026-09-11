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

    /**
     * Resolves a value that may contain a ${key} config placeholder.
     *
     * Used in Cucumber step definitions so feature files can reference
     * config.properties values without hardcoding them:
     *
     *   Feature file:   Given I am logged in as "${usernameOrEmailResonance}"
     *   Step def:       loginPage.login(ConfigReader.resolve(username), ...)
     *   Resolved value: "user1"  (read from config.properties at runtime)
     *
     * If the value does not match the ${...} pattern it is returned unchanged,
     * so literal strings like "wrongpassword" are passed through as-is.
     *
     * @param value raw string from Cucumber step (e.g., "${passwordResonance}" or "wrongpassword")
     * @return resolved value from config, or the original value unchanged
     */
    public static String resolve(String value) {
        if (value != null && value.startsWith("${") && value.endsWith("}")) {
            String key = value.substring(2, value.length() - 1);
            String resolved = properties.getProperty(key);
            if (resolved == null) {
                throw new RuntimeException("Config key not found: " + key);
            }
            return resolved;
        }
        return value;
    }
}
