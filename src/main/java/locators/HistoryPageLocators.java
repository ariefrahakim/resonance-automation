package locators;

import org.openqa.selenium.By;

public class HistoryPageLocators {
    public static final By TICKET_LIST = By.xpath(
            "//div[contains(@class,'ticket') or contains(@class,'card') or contains(@class,'item')]");
    public static final By TICKET_TITLE = By.xpath(
            "//h2 | //h3 | //p[contains(@class,'title')]");
    public static final By STATUS_FILTER = By.xpath(
            "//select | //button[contains(text(),'filter') or contains(text(),'Filter')]");
    public static final By SOLVED_BADGE = By.xpath(
            "//*[contains(text(),'Solved') or contains(@class,'solved')]");
    public static final By EMPTY_STATE = By.xpath(
            "//*[contains(text(),'No ticket') or contains(text(),'Empty') or contains(text(),'Belum ada')]");
    public static final By SORT_SELECT = By.xpath(
            "//select[contains(@name,'order') or contains(@id,'order')]");
}
