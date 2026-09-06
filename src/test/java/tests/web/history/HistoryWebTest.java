package tests.web.history;

import base.BaseWebTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.DriverManager;

public class HistoryWebTest extends BaseWebTest {

    @Test(priority = 1)
    public void testLoginAndNavigateToHistory() {
        doLogin();
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        historyPage.navigate();
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/history"),
                "Should be on history page. Actual URL: " + currentUrl);
        System.out.println("On history page: " + currentUrl);
    }

    @Test(priority = 2, dependsOnMethods = "testLoginAndNavigateToHistory")
    public void testHistoryPageLoaded() {
        String pageSource = DriverManager.getDriver().getPageSource();
        Assert.assertFalse(pageSource.isEmpty(), "History page should not be empty");
        System.out.println("History page content length: " + pageSource.length());
    }

    @Test(priority = 3, dependsOnMethods = "testLoginAndNavigateToHistory")
    public void testHistoryPageHasRelevantContent() {
        String pageSource = DriverManager.getDriver().getPageSource();
        boolean hasContent = pageSource.contains("Ticket") || pageSource.contains("ticket") ||
                pageSource.contains("History") || pageSource.contains("history") ||
                pageSource.contains("Empty") || pageSource.contains("Belum") ||
                pageSource.contains("resonance");
        Assert.assertTrue(hasContent, "History page should have relevant content");
        System.out.println("History page content check passed");
    }
}
