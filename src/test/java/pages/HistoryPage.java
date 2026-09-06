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

    /** Kembalikan semua link tiket di halaman riwayat. */
    public List<WebElement> getTicketLinks() {
        return driver.findElements(HistoryPageLocators.TICKET_LINKS);
    }

    public int getTicketCount() {
        return getTicketLinks().size();
    }

    public void clickBackToDashboard() {
        click(HistoryPageLocators.BACK_DASHBOARD);
    }

    public boolean isOnHistoryPage() {
        return driver.getCurrentUrl().contains("/history");
    }

    public boolean isTicketVisible(String title) {
        return driver.getPageSource().contains(title);
    }
}
