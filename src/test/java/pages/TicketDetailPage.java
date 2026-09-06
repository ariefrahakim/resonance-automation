package pages;

import locators.TicketDetailPageLocators;
import org.openqa.selenium.WebDriver;

public class TicketDetailPage extends BasePage {

    public TicketDetailPage(WebDriver driver) {
        super(driver);
    }

    public void navigate(String ticketId) {
        navigateTo("/ticket/" + ticketId);
    }

    public String getTicketTitle() {
        return getText(TicketDetailPageLocators.TICKET_TITLE);
    }

    public void clickVote() {
        click(TicketDetailPageLocators.VOTE_BUTTON);
    }

    public String getVoteCount() {
        return getText(TicketDetailPageLocators.VOTE_COUNT);
    }

    public void addComment(String comment) {
        type(TicketDetailPageLocators.COMMENT_INPUT, comment);
        click(TicketDetailPageLocators.SUBMIT_COMMENT);
    }

    public boolean isCommentVisible(String commentText) {
        return driver.getPageSource().contains(commentText);
    }

    public void clickMarkSolved() {
        click(TicketDetailPageLocators.MARK_SOLVED_BUTTON);
    }

    public void clickDelete() {
        click(TicketDetailPageLocators.DELETE_BUTTON);
    }
}
