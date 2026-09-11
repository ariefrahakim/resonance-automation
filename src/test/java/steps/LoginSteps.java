package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.LoginPage;
import utils.ConfigReader;
import utils.DriverManager;

/**
 * Step definitions for the login feature.
 *
 * All string parameters that arrive from the feature file are passed through
 * ConfigReader.resolve() before use. This allows feature files to reference
 * config.properties values via ${key} placeholders instead of hardcoding
 * credentials — the same principle as @DataProvider + ConfigReader in API tests.
 *
 * Examples:
 *   Feature file: When I enter username "${usernameOrEmailResonance}"
 *   Step def:     loginPage.enterUsername(ConfigReader.resolve("${usernameOrEmailResonance}"))
 *   Runtime:      loginPage.enterUsername("user1")
 */
public class LoginSteps {

    private final LoginPage loginPage = new LoginPage(DriverManager.getDriver());

    @Given("I am on the login page")
    public void iAmOnLoginPage() {
        loginPage.navigate();
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Expected to be on the login page. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    /**
     * Enters a username into the login form.
     * Supports ${configKey} placeholders — e.g., "${usernameOrEmailResonance}" resolves to
     * the value in config.properties. Literal strings (invalid data) pass through unchanged.
     */
    @When("I enter username {string}")
    public void iEnterUsername(String username) {
        String resolved = ConfigReader.resolve(username);
        if (!resolved.isEmpty()) {
            loginPage.enterUsername(resolved);
        }
    }

    /**
     * Enters a password into the login form.
     * Supports ${configKey} placeholders — e.g., "${passwordResonance}" resolves to
     * the value in config.properties. Literal strings pass through unchanged.
     */
    @When("I enter password {string}")
    public void iEnterPassword(String password) {
        String resolved = ConfigReader.resolve(password);
        if (!resolved.isEmpty()) {
            loginPage.enterPassword(resolved);
        }
    }

    @When("I click the Login button")
    public void iClickLoginButton() {
        loginPage.clickLoginButton();
        sleep(2000);
    }

    @Then("I should be redirected away from the login page")
    public void iShouldBeRedirectedAwayFromLogin() {
        Assert.assertFalse(loginPage.isOnLoginPage(),
                "Expected to leave the login page after successful login. URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("I should be on the dashboard page")
    public void iShouldBeOnDashboard() {
        Assert.assertFalse(loginPage.isOnLoginPage(),
                "Expected to be on the dashboard. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("I should see an error or stay on the login page")
    public void iShouldSeeErrorOrStayOnLogin() {
        boolean onLoginPage = loginPage.isOnLoginPage();
        boolean hasError    = loginPage.isErrorToastDisplayed();
        Assert.assertTrue(onLoginPage || hasError,
                "Expected to stay on login page or see an error. URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("I should stay on the login page")
    public void iShouldStayOnLoginPage() {
        sleep(1500);
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Expected to remain on the login page. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("I should see a link to the register page")
    public void iSeeRegisterLink() {
        Assert.assertTrue(loginPage.isDisplayed(locators.LoginPageLocators.REGISTER_LINK),
                "Register link should be visible on the login page");
    }

    @Then("I should see a forgot password link")
    public void iSeeForgotPasswordLink() {
        Assert.assertTrue(loginPage.isDisplayed(locators.LoginPageLocators.FORGOT_PWD_LINK),
                "Forgot password link should be visible on the login page");
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
