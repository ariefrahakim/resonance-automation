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

    public void clickHistoryNav() {
        click(HomePageLocators.NAV_HISTORY);
    }

    public void clickLogout() {
        click(HomePageLocators.NAV_LOGOUT);
    }

    public boolean isEmptyState() {
        return isDisplayed(HomePageLocators.EMPTY_STATE);
    }

    public boolean isTicketVisible(String title) {
        return driver.getPageSource().contains(title);
    }
}
