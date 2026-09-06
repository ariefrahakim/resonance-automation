package locators;

import org.openqa.selenium.By;

public class TicketDetailPageLocators {
    public static final By TICKET_TITLE = By.xpath(
            "//h1 | //h2[contains(@class,'title')]");
    public static final By TICKET_DESCRIPTION = By.xpath(
            "//p[contains(@class,'description')] | //div[contains(@class,'desc')]");
    public static final By TICKET_STATUS = By.xpath(
            "//*[contains(text(),'Solved') or contains(text(),'Unsolved') or contains(text(),'Open')]");
    public static final By VOTE_BUTTON = By.xpath(
            "//button[contains(@class,'vote') or contains(text(),'Vote') or contains(@aria-label,'vote')]");
    public static final By VOTE_COUNT = By.xpath(
            "//*[contains(@class,'vote-count') or contains(@class,'voteCount')]");
    public static final By COMMENT_INPUT = By.xpath(
            "//textarea[contains(@placeholder,'comment') or contains(@placeholder,'komentar') or @name='body'] | " +
            "//div[@contenteditable='true']");
    public static final By SUBMIT_COMMENT_BUTTON = By.xpath(
            "//button[contains(text(),'Comment') or contains(text(),'Send') or contains(text(),'Kirim')]");
    public static final By COMMENT_LIST = By.xpath(
            "//div[contains(@class,'comment')]");
    public static final By DELETE_TICKET_BUTTON = By.xpath(
            "//button[contains(text(),'Delete') or contains(text(),'Hapus')]");
    public static final By MARK_SOLVED_BUTTON = By.xpath(
            "//button[contains(text(),'Solved') or contains(text(),'Selesai') or contains(text(),'Resolve')]");
    public static final By BACK_BUTTON = By.xpath(
            "//a[contains(@href,'/')] | //button[contains(text(),'Back') or contains(text(),'Kembali')]");
}
