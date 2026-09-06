package locators;

import org.openqa.selenium.By;

public class HomePageLocators {
    public static final By TICKET_LIST = By.xpath(
            "//div[contains(@class,'ticket') or contains(@class,'card') or contains(@class,'item')]");
    public static final By CREATE_TICKET_BUTTON = By.xpath(
            "//a[contains(@href,'/new')] | //button[contains(text(),'New') or contains(text(),'Create') or contains(text(),'Buat')]");
    public static final By SEARCH_INPUT = By.xpath(
            "//input[@type='search' or contains(@placeholder,'search') or contains(@placeholder,'Search') or contains(@placeholder,'cari')]");
    public static final By TICKET_TITLE = By.xpath(
            "//h2 | //h3 | //p[contains(@class,'title')]");
    public static final By NAV_HISTORY = By.xpath(
            "//a[contains(@href,'/history') or contains(text(),'History') or contains(text(),'Riwayat')]");
    public static final By NAV_LOGOUT = By.xpath(
            "//button[contains(text(),'Logout') or contains(text(),'Sign out') or contains(text(),'Keluar')]");
    public static final By EMPTY_STATE = By.xpath(
            "//*[contains(text(),'No ticket') or contains(text(),'Empty') or contains(text(),'Belum')]");
}
