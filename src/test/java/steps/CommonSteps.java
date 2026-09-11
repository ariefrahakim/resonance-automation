package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.HistoryPage;
import pages.HomePage;
import pages.LoginPage;
import pages.NewTicketPage;
import utils.ConfigReader;
import utils.DriverManager;

/**
 * Shared step definitions used across multiple feature files.
 *
 * Login steps here accept ${configKey} placeholders via ConfigReader.resolve()
 * so feature files never hardcode credentials. Example:
 *
 *   Feature file:  Given I am logged in as "${usernameOrEmailResonance}" with password "${passwordResonance}"
 *   Resolved as:   loginPage.login("user1", "password")
 *
 * Invalid credential strings (e.g., "wrongpassword") are passed through unchanged
 * because they do not match the ${...} pattern.
 */
public class CommonSteps {

    private final LoginPage     loginPage     = new LoginPage(DriverManager.getDriver());
    private final HomePage      homePage      = new HomePage(DriverManager.getDriver());
    private final NewTicketPage newTicketPage = new NewTicketPage(DriverManager.getDriver());
    private final HistoryPage   historyPage   = new HistoryPage(DriverManager.getDriver());

    /**
     * Performs a full login. Credentials support ${configKey} placeholders.
     * Valid credentials come from config.properties via ${usernameOrEmailResonance} and ${passwordResonance}.
     */
    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAs(String username, String password) {
        loginPage.login(ConfigReader.resolve(username), ConfigReader.resolve(password));
        sleep(2000);
        Assert.assertFalse(loginPage.isOnLoginPage(),
                "Login failed — still on the login page. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Given("I am not logged in")
    public void iAmNotLoggedIn() {
        DriverManager.getDriver().manage().deleteAllCookies();
    }

    @Given("I am on the history page")
    public void iAmOnHistoryPage() {
        historyPage.navigate();
        sleep(1500);
        Assert.assertTrue(historyPage.isOnHistoryPage(),
                "Expected to be on the history page. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @When("I am on the dashboard page")
    public void iNavigateToDashboard() {
        String url = DriverManager.getDriver().getCurrentUrl();
        if (url.contains("/login")) {
            homePage.navigate();
            sleep(1500);
        }
        Assert.assertFalse(DriverManager.getDriver().getCurrentUrl().contains("/login"),
                "Expected to be on the dashboard. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @When("I access the new ticket page directly")
    public void iAccessNewTicketDirectly() {
        newTicketPage.navigate();
        sleep(2000);
    }

    @When("I access the history page directly")
    public void iAccessHistoryDirectly() {
        historyPage.navigate();
        sleep(2000);
    }

    @When("I access the ticket page with ID {string}")
    public void iAccessTicketById(String ticketId) {
        DriverManager.getDriver().get(ConfigReader.getProperty("webUrl") + "/ticket/" + ticketId);
        sleep(2000);
    }

    @Then("I should be redirected to the login page")
    public void iAmRedirectedToLogin() {
        String url        = DriverManager.getDriver().getCurrentUrl();
        String pageSource = DriverManager.getDriver().getPageSource();
        boolean onLogin   = url.contains("/login") || pageSource.contains("btn-login");
        Assert.assertTrue(onLogin,
                "Expected to be redirected to the login page. URL: " + url);
    }

    @Then("the page shows an error or redirects")
    public void pageShowsErrorOrRedirects() {
        String pageSource = DriverManager.getDriver().getPageSource();
        String url        = DriverManager.getDriver().getCurrentUrl();
        boolean hasError  = pageSource.contains("404") || pageSource.contains("not found")
                || pageSource.contains("Error") || pageSource.contains("error");
        boolean redirected = !url.contains("id-tidak-valid-xyz-123");
        Assert.assertTrue(hasError || redirected,
                "Expected an error or redirect. URL: " + url);
    }

    @Then("the page content is not empty")
    public void pageContentIsNotEmpty() {
        String pageSource = DriverManager.getDriver().getPageSource();
        Assert.assertFalse(pageSource.isEmpty(), "Page content should not be empty");
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
