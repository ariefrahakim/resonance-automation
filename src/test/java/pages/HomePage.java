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

    /**
     * Clicks the create-ticket button via JS to bypass any animation overlay in CI headless Chrome.
     * The plain Selenium click registers the button as clickable but the navigation doesn't fire
     * reliably in CI — JS click ensures the event reaches the Next.js handler.
     */
    public void clickCreateTicket() {
        jsClick(HomePageLocators.CREATE_TICKET_BUTTON);
    }

    /** Types the given keyword into the ticket search input. */
    public void searchTicket(String keyword) {
        type(HomePageLocators.SEARCH_INPUT, keyword);
    }

    /**
     * Opens the navbar hamburger, waits for the history link to be visible, then clicks it via JS.
     * In CI headless Chrome the Chakra UI drawer animation can exceed the default clickable timeout,
     * so we wait for visibility explicitly before clicking.
     */
    public void clickHistoryNav() {
        jsClick(HomePageLocators.NAV_TOGGLE);
        waitForElement(HomePageLocators.NAV_HISTORY);
        jsClick(HomePageLocators.NAV_HISTORY);
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
