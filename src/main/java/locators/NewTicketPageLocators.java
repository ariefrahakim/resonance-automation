package locators;

import org.openqa.selenium.By;

public class NewTicketPageLocators {
    public static final By TITLE_INPUT = By.xpath(
            "//input[@name='title' or @id='title' or contains(@placeholder,'title') or contains(@placeholder,'judul')]");
    public static final By DESCRIPTION_INPUT = By.xpath(
            "//textarea[@name='description' or @id='description'] | //div[@role='textbox']");
    public static final By IS_PUBLIC_TOGGLE = By.xpath(
            "//input[@type='checkbox'] | //div[contains(@class,'switch') or contains(@class,'toggle')]");
    public static final By SUBMIT_BUTTON = By.xpath(
            "//button[@type='submit' or contains(text(),'Submit') or contains(text(),'Kirim') or contains(text(),'Create') or contains(text(),'Buat')]");
    public static final By CANCEL_BUTTON = By.xpath(
            "//button[contains(text(),'Cancel') or contains(text(),'Batal')]");
    public static final By SUCCESS_MESSAGE = By.xpath(
            "//*[contains(@class,'success') or contains(text(),'berhasil') or contains(text(),'created')]");
}
