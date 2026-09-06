package tests.web.ticket;

import base.BaseWebTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.DriverManager;

public class ViewTicketWebTest extends BaseWebTest {

    @Test(priority = 1)
    public void testLoginAndNavigateHome() {
        doLogin();
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        homePage.navigate();
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("/login"),
                "Should be on home page, not login. URL: " + currentUrl);
        System.out.println("Home page loaded: " + currentUrl);
    }

    @Test(priority = 2, dependsOnMethods = "testLoginAndNavigateHome")
    public void testHomePageHasContent() {
        String pageSource = DriverManager.getDriver().getPageSource();
        Assert.assertFalse(pageSource.isEmpty(), "Home page should not be empty");
        System.out.println("Home page content length: " + pageSource.length());
    }

    @Test(priority = 3, dependsOnMethods = "testLoginAndNavigateHome")
    public void testPageTitle() {
        String title = DriverManager.getDriver().getTitle();
        System.out.println("Page title: " + title);
        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");
    }
}
