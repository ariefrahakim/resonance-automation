package locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the ticket history page (/history).
 *
 * Locator priority (most stable to most fragile):
 *   1. By.id()          — direct match on the id="" attribute
 *   2. By.cssSelector() — use href/data attributes, never Chakra hash classes
 *   3. By.xpath()       — for text content or parent-child relationships
 */
public class HistoryPageLocators {

    /** Returns to the dashboard — stable explicit id. */
    public static final By BACK_DASHBOARD = By.id("btn-dashboard");
}
