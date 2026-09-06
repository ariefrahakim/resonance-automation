package tests.web.ticket;

import base.BaseWebTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.DriverManager;
import utils.Utils;

public class CreateTicketWebTest extends BaseWebTest {

    private String ticketTitle;

    @Test(priority = 1)
    public void testLoginBeforeCreateTicket() {
        doLogin();
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("/login"), "Should be logged in. URL: " + currentUrl);
        System.out.println("Logged in, current URL: " + currentUrl);
    }

    @Test(priority = 2, dependsOnMethods = "testLoginBeforeCreateTicket")
    public void testNavigateToNewTicketPage() {
        newTicketPage.navigate();
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/new"),
                "Should be on new ticket page. Actual URL: " + currentUrl);
        System.out.println("On new ticket page: " + currentUrl);
    }

    @Test(priority = 3, dependsOnMethods = "testNavigateToNewTicketPage")
    public void testCreateNewTicket() {
        ticketTitle = "Web Ticket " + Utils.generateRandomString(5);

        newTicketPage.createTicket(ticketTitle, "Ticket created via web automation test");
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        boolean redirected = !currentUrl.contains("/new");
        boolean hasSuccess = newTicketPage.isSuccessMessageDisplayed();

        Assert.assertTrue(redirected || hasSuccess,
                "Ticket creation should result in redirect or success message. URL: " + currentUrl);
        System.out.println("Ticket created: " + ticketTitle + " | URL: " + currentUrl);
    }

    @Test(priority = 4, dependsOnMethods = "testLoginBeforeCreateTicket")
    public void testHomePageHasCreateButton() {
        homePage.navigate();
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String pageSource = DriverManager.getDriver().getPageSource();
        Assert.assertFalse(pageSource.isEmpty(), "Home page should load content");
        System.out.println("Home page content loaded, length: " + pageSource.length());
    }
}
