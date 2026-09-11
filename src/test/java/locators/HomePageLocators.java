package locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the Home / Dashboard page (/).
 * All primary buttons have explicit IDs (btn-*).
 * The ticket list uses an href pattern — stable and independent of Chakra UI hash classes.
 */
public class HomePageLocators {

    // Search & create — both have explicit IDs
    public static final By SEARCH_INPUT         = By.id("input-search-ticket");
    public static final By CREATE_TICKET_BUTTON = By.id("btn-create-ticket");
    public static final By CREATE_TICKET_LINK   = By.cssSelector("a[href='/new']");

    /**
     * Ticket list: anchors pointing to /ticket/ — stable, href-attribute based.
     * Does not rely on Chakra UI hash classes.
     */
    public static final By TICKET_LIST          = By.cssSelector("a[href*='/ticket/']");

    /**
     * Ticket title: the first span inside each ticket link.
     */
    public static final By TICKET_TITLE         = By.xpath("//a[contains(@href,'/ticket/')]//span[1]");

    // Filters — all have explicit IDs
    public static final By FILTER_VOTE          = By.id("btn-filter-order-vote");
    public static final By FILTER_NEWEST        = By.id("btn-filter-order-newest");
    public static final By FILTER_SOLVE         = By.id("btn-filter-order-solve");

    // Navbar — explicit IDs; NAV_TOGGLE must be clicked before any other navbar action
    public static final By NAV_TOGGLE           = By.id("btn-open-navbar");
    public static final By NAV_DASHBOARD        = By.id("btn-nav-dashboard");
    public static final By NAV_HISTORY          = By.id("btn-nav-history");
    public static final By NAV_LOGOUT           = By.id("btn-logout");
    public static final By NAV_CLOSE            = By.id("btn-close-navbar");

    // Pagination — explicit IDs
    public static final By PAGINATION_PREV      = By.id("btn-pagination-prev");
    public static final By PAGINATION_NEXT      = By.id("btn-pagination-next");
}
