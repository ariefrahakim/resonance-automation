package tests.api.auth;

import base.BaseApiTest;
import body.auth.LoginBody;
import data.LoginDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.JsonFileManager;

/**
 * Test class for the Resonance authentication endpoint.
 * Covers successful and failed login scenarios.
 */
public class LoginApiTest extends BaseApiTest {

    /**
     * TC-AUTH-001: Verifies a successful login with valid credentials.
     * The returned JWT token is saved for use by subsequent tests.
     */
    @Test(priority = 1, description = "Successful login with valid credentials")
    public void testLoginSuccess() {
        String username = ConfigReader.getProperty("usernameOrEmailResonance");
        String password = ConfigReader.getProperty("passwordResonance");

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        // Status code must be 200
        Assert.assertEquals(response.getStatusCode(), 200, "Status code must be 200 for a successful login");

        // Bearer token must not be null
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Bearer token must be returned after a successful login");

        // Save token to file — used as Authorization: Bearer <token> in subsequent tests
        JsonFileManager.writeValue(TOKEN_FILE, "token", token);
        System.out.println("[PASS] Bearer token saved: " + token.substring(0, Math.min(20, token.length())) + "...");
    }

    /**
     * TC-AUTH-002: Verifies successful login using a DataProvider (data-driven).
     * Runs login with various valid data combinations.
     */
    @Test(priority = 2,
          dataProvider = "loginValidData",
          dataProviderClass = LoginDataProvider.class,
          description = "Login with various valid credential formats")
    public void testLoginWithValidData(String username, String password, String scenario) {
        System.out.println("[INFO] Scenario: " + scenario);

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Login must succeed for scenario: " + scenario);
        Assert.assertNotNull(response.jsonPath().getString("token"),
                "Token must be returned for scenario: " + scenario);

        System.out.println("[PASS] " + scenario + " - Login successful");
    }

    /**
     * TC-AUTH-003: Verifies login failure with invalid data (data-driven).
     * Each combination must return a status code other than 200.
     */
    @Test(priority = 3,
          dataProvider = "loginInvalidData",
          dataProviderClass = LoginDataProvider.class,
          description = "Login fails with various invalid data combinations")
    public void testLoginWithInvalidData(String username, String password, String scenario) {
        System.out.println("[INFO] Negative scenario: " + scenario);

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        // Login with invalid data must NOT return status 200
        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Login should fail for scenario: " + scenario);

        System.out.println("[PASS] " + scenario + " - Status: " + response.getStatusCode());
    }
}
