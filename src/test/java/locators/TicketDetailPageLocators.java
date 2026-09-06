package locators;

import org.openqa.selenium.By;

/**
 * Locator terpusat untuk halaman detail tiket (/ticket/[id]).
 */
public class TicketDetailPageLocators {
    // Ticket info
    public static final By TICKET_TITLE         = By.cssSelector("h1, h2");
    public static final By TICKET_STATUS        = By.cssSelector("[class*='badge'], [class*='status']");

    // Vote
    public static final By VOTE_BUTTON          = By.cssSelector("button[aria-label*='vote'], button[id*='vote']");
    public static final By VOTE_COUNT           = By.cssSelector("[id*='vote-count'], [class*='voteCount']");

    // Comments
    public static final By COMMENT_INPUT        = By.cssSelector("textarea[placeholder*='comment'], textarea[id*='comment']");
    public static final By SUBMIT_COMMENT       = By.cssSelector("button[id*='submit-comment'], button[type='submit']");
    public static final By COMMENT_LIST         = By.cssSelector("[class*='comment']");

    // Actions
    public static final By MARK_SOLVED_BUTTON   = By.cssSelector("button[id*='solve'], button[id*='solved']");
    public static final By DELETE_BUTTON        = By.cssSelector("button[id*='delete']");
    public static final By BACK_BUTTON          = By.cssSelector("button[id*='back'], a[href='/']");

    // Toast
    public static final By SUCCESS_TOAST        = By.cssSelector("li.chakra-toast .chakra-alert[data-status='success']");
}
