package pages;

import locators.HomePageLocators;
import org.openqa.selenium.WebDriver;

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

    /** Opens the navbar hamburger menu first, then clicks the History link. */
    public void clickHistoryNav() {
        click(HomePageLocators.NAV_TOGGLE);
        click(HomePageLocators.NAV_HISTORY);
    }

    /** Applies the "by votes" sort filter. */
    public void filterByVote() {
        click(HomePageLocators.FILTER_VOTE);
    }

    /** Applies the "newest" sort filter. */
    public void filterByNewest() {
        click(HomePageLocators.FILTER_NEWEST);
    }
}
