package pages;

import locators.LoginPageLocators;
import org.openqa.selenium.WebDriver;

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

    public boolean isErrorDisplayed() {
        return isDisplayed(LoginPageLocators.ERROR_MESSAGE);
    }

    public void clickRegisterLink() {
        click(LoginPageLocators.REGISTER_LINK);
    }

    public void clickForgotPasswordLink() {
        click(LoginPageLocators.FORGOT_PASSWORD_LINK);
    }
}
