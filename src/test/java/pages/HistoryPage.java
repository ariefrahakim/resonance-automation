package pages;

import locators.HistoryPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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

    /** Returns all ticket links on the history page. */
    public List<WebElement> getTicketLinks() {
        return driver.findElements(HistoryPageLocators.TICKET_LINKS);
    }

    /** Returns the number of ticket links currently visible on the history page. */
    public int getTicketCount() {
        return getTicketLinks().size();
    }

    /** Clicks the back-to-dashboard button. */
    public void clickBackToDashboard() {
        click(HistoryPageLocators.BACK_DASHBOARD);
    }

    /** Returns {@code true} if the current URL corresponds to the history page. */
    public boolean isOnHistoryPage() {
        return driver.getCurrentUrl().contains("/history");
    }

    /** Returns {@code true} if the given ticket title appears anywhere in the page source. */
    public boolean isTicketVisible(String title) {
        return driver.getPageSource().contains(title);
    }
}
