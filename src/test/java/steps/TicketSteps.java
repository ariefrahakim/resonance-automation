package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.HomePage;
import pages.NewTicketPage;
import utils.DriverManager;
import utils.Utils;

/**
 * Step definitions for ticket creation and ticket list features.
 */
public class TicketSteps {

    private final HomePage      homePage      = new HomePage(DriverManager.getDriver());
    private final NewTicketPage newTicketPage = new NewTicketPage(DriverManager.getDriver());

    @When("I click the Create Ticket button")
    public void iClickCreateTicket() {
        homePage.clickCreateTicket();
        sleep(1500);
    }

    @Then("I should be on the new ticket page")
    public void iAmOnNewTicketPage() {
        Assert.assertTrue(newTicketPage.isOnNewPage(),
                "Expected to be on /new page. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @When("I enter the ticket title {string}")
    public void iEnterTicketTitle(String title) {
        // Append a random suffix so each run creates a unique ticket title
        String uniqueTitle = title + " - " + Utils.generateRandomString(4);
        newTicketPage.enterTitle(uniqueTitle);
    }

    @And("I enter the ticket description {string}")
    public void iEnterTicketDescription(String description) {
        newTicketPage.enterDescription(description);
    }

    @And("I select the Private option")
    public void iSelectPrivateOption() {
        newTicketPage.selectPrivate();
    }

    @And("I select the Public option")
    public void iSelectPublicOption() {
        newTicketPage.selectPublic();
    }

    @When("I click Submit Ticket")
    public void iClickSubmitTicket() {
        newTicketPage.clickSubmit();
        sleep(2000);
    }

    @Then("the ticket should be created successfully")
    public void ticketIsCreatedSuccessfully() {
        boolean redirected = !newTicketPage.isOnNewPage();
        boolean hasSuccess = newTicketPage.isSuccessToastDisplayed();
        Assert.assertTrue(redirected || hasSuccess,
                "Expected ticket creation to succeed (redirect or success toast). URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("ticket creation should fail and I should stay on the new ticket page")
    public void ticketCreationFailsAndStaysOnPage() {
        sleep(1000);
        boolean stayedOnPage = newTicketPage.isOnNewPage();
        boolean hasError     = newTicketPage.isErrorToastDisplayed();
        Assert.assertTrue(stayedOnPage || hasError,
                "Expected ticket creation with empty title to fail. URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("the dashboard should be loaded successfully")
    public void dashboardLoaded() {
        String url = DriverManager.getDriver().getCurrentUrl();
        Assert.assertFalse(url.contains("/login"),
                "Expected dashboard to load successfully. URL: " + url);
    }

    @When("I filter tickets by order {string}")
    public void iFilterByOrder(String order) {
        switch (order.toLowerCase()) {
            case "vote":
                homePage.filterByVote();
                break;
            case "newest":
                homePage.filterByNewest();
                break;
            default:
                throw new IllegalArgumentException("Unknown order value: " + order);
        }
        sleep(1000);
    }

    @When("I search for tickets with keyword {string}")
    public void iSearchTicket(String keyword) {
        homePage.searchTicket(keyword);
        sleep(1000);
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
