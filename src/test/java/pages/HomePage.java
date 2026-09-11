package pages;

import locators.HomePageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page object for the Home / Dashboard page (/).
 */
public class HomePage extends BasePage {

    /** Initialises the HomePage with the given WebDriver instance. */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    /** Navigates the browser to the home/dashboard page. */
    public void navigate() {
        navigateTo("/");
    }

    /** Clicks the create-ticket button to open the new-ticket form. */
    public void clickCreateTicket() {
        click(HomePageLocators.CREATE_TICKET_BUTTON);
    }

    /** Types the given keyword into the ticket search input. */
    public void searchTicket(String keyword) {
        type(HomePageLocators.SEARCH_INPUT, keyword);
    }

    /** Returns all ticket link elements currently visible on the page. */
    public List<WebElement> getTicketList() {
        return driver.findElements(HomePageLocators.TICKET_LIST);
    }

    /** Returns the number of ticket links currently visible on the page. */
    public int getTicketCount() {
        return getTicketList().size();
    }

    /** Opens the navbar hamburger menu first, then clicks the History link. */
    public void clickHistoryNav() {
        click(HomePageLocators.NAV_TOGGLE);
        click(HomePageLocators.NAV_HISTORY);
    }

    /** Opens the navbar hamburger menu first, then clicks the Logout button. */
    public void clickLogout() {
        click(HomePageLocators.NAV_TOGGLE);
        click(HomePageLocators.NAV_LOGOUT);
    }

    /** Applies the "by votes" sort filter. */
    public void filterByVote() {
        click(HomePageLocators.FILTER_VOTE);
    }

    /** Applies the "newest" sort filter. */
    public void filterByNewest() {
        click(HomePageLocators.FILTER_NEWEST);
    }

    /** Returns {@code true} if the given ticket title appears anywhere in the page source. */
    public boolean isTicketVisible(String title) {
        return driver.getPageSource().contains(title);
    }

    /** Returns {@code true} if the current URL corresponds to the home/dashboard page. */
    public boolean isOnHomePage() {
        String url = driver.getCurrentUrl();
        return !url.contains("/login") && !url.contains("/new") && !url.contains("/history");
    }
}
