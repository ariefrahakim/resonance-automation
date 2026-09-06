package pages;

import locators.HomePageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void navigate() {
        navigateTo("/");
    }

    public void clickCreateTicket() {
        click(HomePageLocators.CREATE_TICKET_BUTTON);
    }

    public void searchTicket(String keyword) {
        type(HomePageLocators.SEARCH_INPUT, keyword);
    }

    public List<WebElement> getTicketList() {
        return driver.findElements(HomePageLocators.TICKET_LIST);
    }

    public int getTicketCount() {
        return getTicketList().size();
    }

    /** Klik tombol navbar hamburger dulu, baru klik History. */
    public void clickHistoryNav() {
        click(HomePageLocators.NAV_TOGGLE);
        click(HomePageLocators.NAV_HISTORY);
    }

    /** Klik tombol navbar hamburger dulu, baru klik Logout. */
    public void clickLogout() {
        click(HomePageLocators.NAV_TOGGLE);
        click(HomePageLocators.NAV_LOGOUT);
    }

    public void filterByVote() {
        click(HomePageLocators.FILTER_VOTE);
    }

    public void filterByNewest() {
        click(HomePageLocators.FILTER_NEWEST);
    }

    public boolean isTicketVisible(String title) {
        return driver.getPageSource().contains(title);
    }

    public boolean isOnHomePage() {
        String url = driver.getCurrentUrl();
        return !url.contains("/login") && !url.contains("/new") && !url.contains("/history");
    }
}
