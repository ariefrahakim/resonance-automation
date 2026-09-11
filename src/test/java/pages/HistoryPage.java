package pages;

import locators.HistoryPageLocators;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the ticket history page (/history).
 */
public class HistoryPage extends BasePage {

    /** Initialises the HistoryPage with the given WebDriver instance. */
    public HistoryPage(WebDriver driver) {
        super(driver);
    }

    /** Navigates the browser to the history page. */
    public void navigate() {
        navigateTo("/history");
    }

    /** Clicks the back-to-dashboard button. */
    public void clickBackToDashboard() {
        click(HistoryPageLocators.BACK_DASHBOARD);
    }

    /** Returns {@code true} if the current URL corresponds to the history page. */
    public boolean isOnHistoryPage() {
        return driver.getCurrentUrl().contains("/history");
    }
}
