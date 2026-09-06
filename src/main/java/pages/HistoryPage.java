package pages;

import locators.HistoryPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HistoryPage extends BasePage {

    public HistoryPage(WebDriver driver) {
        super(driver);
    }

    public void navigate() {
        navigateTo("/history");
    }

    public List<WebElement> getTicketList() {
        return driver.findElements(HistoryPageLocators.TICKET_LIST);
    }

    public boolean isEmptyState() {
        return isDisplayed(HistoryPageLocators.EMPTY_STATE);
    }

    public boolean isTicketVisible(String title) {
        return driver.getPageSource().contains(title);
    }

    public boolean isSolvedBadgeVisible() {
        return isDisplayed(HistoryPageLocators.SOLVED_BADGE);
    }
}
