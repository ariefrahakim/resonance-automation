package tests.web.auth;

import base.BaseWebTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.DriverManager;

public class LoginWebTest extends BaseWebTest {

    @Test(priority = 1)
    public void testLoginPageElements() {
        loginPage.navigate();
        String pageSource = DriverManager.getDriver().getPageSource();
        Assert.assertTrue(
                pageSource.toLowerCase().contains("login") || pageSource.contains("Log in"),
                "Login page should contain login-related text");
        System.out.println("Login page loaded: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Test(priority = 2)
    public void testLoginSuccess() {
        String username = ConfigReader.getProperty("usernameOrEmailResonance");
        String password = ConfigReader.getProperty("passwordResonance");

        loginPage.login(username, password);

        // Wait briefly for redirect
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("/login"),
                "Should be redirected away from login after successful login. URL: " + currentUrl);
        System.out.println("Login successful, now at: " + currentUrl);
    }

    @Test(priority = 3)
    public void testLoginWithInvalidCredentials() {
        loginPage.login("invalid@test.com", "wrongpass");

        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        boolean stayedOnLogin = currentUrl.contains("/login");
        boolean hasError = loginPage.isErrorDisplayed();

        Assert.assertTrue(stayedOnLogin || hasError,
                "Should show error or stay on login page for invalid credentials");
        System.out.println("Invalid login handled. URL: " + currentUrl + " | Error shown: " + hasError);
    }
}
