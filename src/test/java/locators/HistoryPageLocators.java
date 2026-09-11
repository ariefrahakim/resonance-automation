package locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the ticket history page (/history).
 *
 * Locator selection principles (most stable to most fragile):
 *   1. By.id()           — most stable; targets the id="" attribute directly
 *   2. By.name()         — targets the name="" attribute
 *   3. By.cssSelector()  — use href/structural attributes, NOT Chakra hash classes
 *   4. By.xpath()        — for text content or parent/child relationships not expressible in CSS
 *
 * Avoid: hash classes like css-n0wfye, css-1gu0mm2 — they change on every build.
 */
public class HistoryPageLocators {

    /**
     * Ticket list: anchors pointing to /ticket/ — stable because they are href-based.
     * Every ticket always has a link to /ticket/{id}.
     */
    public static final By TICKET_LINKS     = By.cssSelector("a[href*='/ticket/']");

    /**
     * Ticket title: the first span inside a ticket link.
     * XPath is more precise here because CSS cannot select the first child of a specific type.
     */
    public static final By TICKET_TITLE     = By.xpath("//a[contains(@href,'/ticket/')]//span[1]");

    /**
     * Ticket container: the parent element of a ticket link, navigated to via XPath.
     */
    public static final By TICKET_CONTAINER = By.xpath("//a[contains(@href,'/ticket/')]/parent::*");

    // Page controls — stable because they use explicit IDs
    public static final By BACK_DASHBOARD   = By.id("btn-dashboard");
    public static final By ITEMS_PER_PAGE   = By.id("select-items-per-page");
    public static final By PAGINATION_PREV  = By.id("btn-pagination-prev");
    public static final By PAGINATION_NEXT  = By.id("btn-pagination-next");
}
