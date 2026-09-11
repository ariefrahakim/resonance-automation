package pages;

import locators.LoginPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for the Login page (/login).
 */
public class LoginPage extends BasePage {

    /** Initialises the LoginPage with the given WebDriver instance. */
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Navigates the browser to the login page. */
    public void navigate() {
        navigateTo("/login");
    }

    /** Types the given username into the email/username field. */
    public void enterUsername(String username) {
        type(LoginPageLocators.USERNAME_INPUT, username);
    }

    /** Types the given password into the password field. */
    public void enterPassword(String password) {
        type(LoginPageLocators.PASSWORD_INPUT, password);
    }

    /** Clicks the login submit button. */
    public void clickLoginButton() {
        click(LoginPageLocators.LOGIN_BUTTON);
    }

    /** Navigates to the login page and submits the given credentials. */
    public void login(String username, String password) {
        navigate();
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Waits up to 15 seconds for the browser to navigate away from /login.
     * Use this after calling login() instead of a fixed Thread.sleep —
     * CI runners are slower than local machines and a fixed 2s sleep causes flaky failures.
     */
    public void waitForLoginRedirect() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !d.getCurrentUrl().contains("/login"));
    }

    /** Waits for a Chakra UI error toast to appear and returns its text. */
    public String getErrorToastText() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.ERROR_TOAST));
            return driver.findElement(LoginPageLocators.ERROR_TOAST).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /** Returns {@code true} if an error toast is currently visible. */
    public boolean isErrorToastDisplayed() {
        return !getErrorToastText().isEmpty();
    }

    /** Returns {@code true} if the current URL contains "/login". */
    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }

    /** Clicks the register navigation link. */
    public void clickRegisterLink() {
        click(LoginPageLocators.REGISTER_LINK);
    }

    /** Clicks the forgot-password navigation link. */
    public void clickForgotPasswordLink() {
        click(LoginPageLocators.FORGOT_PWD_LINK);
    }
}
