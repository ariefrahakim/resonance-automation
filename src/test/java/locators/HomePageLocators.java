package locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the Home / Dashboard page (/).
 * All primary controls have explicit IDs — stable and independent of Chakra UI hash classes.
 */
public class HomePageLocators {

    public static final By SEARCH_INPUT         = By.id("input-search-ticket");
    public static final By CREATE_TICKET_BUTTON = By.id("btn-create-ticket");

    public static final By FILTER_VOTE          = By.id("btn-filter-order-vote");
    public static final By FILTER_NEWEST        = By.id("btn-filter-order-newest");

    /** NAV_TOGGLE must be clicked before NAV_HISTORY is accessible. */
    public static final By NAV_TOGGLE           = By.id("btn-open-navbar");
    public static final By NAV_HISTORY          = By.id("btn-nav-history");
}
