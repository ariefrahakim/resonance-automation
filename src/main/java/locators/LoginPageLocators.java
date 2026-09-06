package locators;

import org.openqa.selenium.By;

public class LoginPageLocators {
    public static final By USERNAME_INPUT = By.xpath(
            "//input[@name='usernameOrEmail' or @id='usernameOrEmail' or @type='text']");
    public static final By PASSWORD_INPUT = By.xpath(
            "//input[@type='password']");
    public static final By LOGIN_BUTTON = By.xpath(
            "//button[@type='submit']");
    public static final By ERROR_MESSAGE = By.xpath(
            "//*[contains(@class,'error') or contains(@class,'alert') or " +
            "contains(text(),'Invalid') or contains(text(),'invalid') or contains(text(),'wrong')]");
    public static final By REGISTER_LINK = By.xpath(
            "//a[contains(@href,'/register')]");
    public static final By FORGOT_PASSWORD_LINK = By.xpath(
            "//a[contains(@href,'/reset-password')]");
}
