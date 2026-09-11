package pages;

import locators.TicketDetailPageLocators;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the ticket detail page (/ticket/[id]).
 */
public class TicketDetailPage extends BasePage {

    /** Initialises the TicketDetailPage with the given WebDriver instance. */
    public TicketDetailPage(WebDriver driver) {
        super(driver);
    }

    /** Navigates the browser to the detail page of the given ticket. */
    public void navigate(String ticketId) {
        navigateTo("/ticket/" + ticketId);
    }

    /** Returns the visible title text of the ticket. */
    public String getTicketTitle() {
        return getText(TicketDetailPageLocators.TICKET_TITLE);
    }

    /** Clicks the vote button on the ticket detail page. */
    public void clickVote() {
        click(TicketDetailPageLocators.VOTE_BUTTON);
    }

    /** Returns the current vote count displayed on the page. */
    public String getVoteCount() {
        return getText(TicketDetailPageLocators.VOTE_COUNT);
    }

    /** Types the given comment text and submits it. */
    public void addComment(String comment) {
        type(TicketDetailPageLocators.COMMENT_INPUT, comment);
        click(TicketDetailPageLocators.SUBMIT_COMMENT);
    }

    /** Returns {@code true} if the given comment text appears anywhere in the page source. */
    public boolean isCommentVisible(String commentText) {
        return driver.getPageSource().contains(commentText);
    }

    /** Clicks the "Mark as Solved" button. */
    public void clickMarkSolved() {
        click(TicketDetailPageLocators.MARK_SOLVED_BUTTON);
    }

    /** Clicks the delete button for the ticket. */
    public void clickDelete() {
        click(TicketDetailPageLocators.DELETE_BUTTON);
    }
}
