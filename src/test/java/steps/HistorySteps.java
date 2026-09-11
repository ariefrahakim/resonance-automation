package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.HistoryPage;
import pages.HomePage;
import utils.DriverManager;

/**
 * Step definitions for the ticket history feature.
 */
public class HistorySteps {

    private final HomePage    homePage    = new HomePage(DriverManager.getDriver());
    private final HistoryPage historyPage = new HistoryPage(DriverManager.getDriver());

    @When("I click History in the navbar")
    public void iClickHistoryInNavbar() {
        homePage.clickHistoryNav();
        sleep(1500);
    }

    @Then("I should be on the history page")
    public void iShouldBeOnHistoryPage() {
        String url = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("/history"),
                "Expected to be on the history page. URL: " + url);
    }

    @Then("the history page should be loaded successfully")
    public void historyPageLoaded() {
        String url = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("/history"),
                "Expected the history page to load successfully. URL: " + url);
    }

    @Then("the history page content should not be empty")
    public void historyPageContentNotEmpty() {
        String pageSource = DriverManager.getDriver().getPageSource();
        Assert.assertFalse(pageSource.isEmpty(), "History page content should not be empty");
    }

    @When("I click the back to Dashboard button")
    public void iClickBackToDashboard() {
        historyPage.clickBackToDashboard();
        sleep(1500);
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
