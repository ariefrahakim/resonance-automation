package data;

import org.testng.annotations.DataProvider;
import utils.ConfigReader;
import utils.Utils;

/**
 * TestNG DataProvider for login endpoint test scenarios.
 *
 * Demonstrates data-driven testing (data binding) by supplying multiple
 * input rows to a single @Test method. TestNG calls the method once per row.
 *
 * Valid credentials are read from config.properties via ConfigReader so
 * they are never hardcoded in test code.
 */
public class LoginDataProvider {

    /**
     * Positive login scenarios — all combinations that should return HTTP 200.
     *
     * Columns: [usernameOrEmail, password, scenarioDescription]
     *
     * Credentials are sourced from config.properties:
     *   usernameOrEmailResonance = user1
     *   passwordResonance        = password
     */
    @DataProvider(name = "loginValidData")
    public static Object[][] loginValidData() {
        String validUser = ConfigReader.getProperty("usernameOrEmailResonance");
        String validPass = ConfigReader.getProperty("passwordResonance");

        return new Object[][] {
            { validUser, validPass, "Login with valid username from config.properties" },
        };
    }

    /**
     * Negative login scenarios — combinations that should NOT return HTTP 200.
     *
     * Columns: [usernameOrEmail, password, scenarioDescription]
     *
     * Covers API validation rules from the OpenAPI spec:
     *   - password minLength: 4
     *   - password maxLength: 12
     *   - usernameOrEmail: required (non-empty)
     *
     * The valid username is read from config so changes in one place propagate here.
     */
    @DataProvider(name = "loginInvalidData")
    public static Object[][] loginInvalidData() {
        String validUser = ConfigReader.getProperty("usernameOrEmailResonance");

        return new Object[][] {
            // Unregistered email — does not exist in the system
            { Utils.generateRandomEmail(),  "password",        "Unregistered email (random generated)" },
            // Wrong password for an existing user
            { validUser,                    "wrongpassword",   "Wrong password for existing user" },
            // Empty username — required field validation
            { "",                           "password",        "Empty username (required field)" },
            // Empty password — required field validation
            { validUser,                    "",                "Empty password (required field)" },
            // Password too short (< 4 chars per API minLength rule)
            { validUser,                    "ab",              "Password too short (< 4 chars, minLength violation)" },
            // Password too long (> 12 chars per API maxLength rule)
            { validUser,                    "passwordyang13ch", "Password too long (> 12 chars, maxLength violation)" },
            // Both fields empty
            { "",                           "",                "Both username and password empty" },
            // Non-email string without @ symbol
            { "notanemailformat",           "password",        "Username without @ symbol (invalid format)" },
            // Unregistered username — looks valid but does not exist in the system
            { "test",                       "password",        "Unregistered username 'test' (valid format, not registered)" },
        };
    }
}
