package pages;

import locators.LoginPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigate() {
        navigateTo("/login");
    }

    public void enterUsername(String username) {
        type(LoginPageLocators.USERNAME_INPUT, username);
    }

    public void enterPassword(String password) {
        type(LoginPageLocators.PASSWORD_INPUT, password);
    }

    public void clickLoginButton() {
        click(LoginPageLocators.LOGIN_BUTTON);
    }

    public void login(String username, String password) {
        navigate();
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /** Menunggu toast error Chakra UI muncul, lalu kembalikan teksnya. */
    public String getErrorToastText() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.ERROR_TOAST));
            return driver.findElement(LoginPageLocators.ERROR_TOAST).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorToastDisplayed() {
        return !getErrorToastText().isEmpty();
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }

    public void clickRegisterLink() {
        click(LoginPageLocators.REGISTER_LINK);
    }

    public void clickForgotPasswordLink() {
        click(LoginPageLocators.FORGOT_PWD_LINK);
    }
}
