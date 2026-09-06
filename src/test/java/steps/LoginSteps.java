package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.LoginPage;
import utils.DriverManager;

/**
 * Step definitions untuk fitur login web.
 */
public class LoginSteps {

    private final LoginPage loginPage = new LoginPage(DriverManager.getDriver());

    @Given("saya berada di halaman login")
    public void iAmOnLoginPage() {
        loginPage.navigate();
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Harus berada di halaman login. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @When("saya memasukkan username {string}")
    public void iEnterUsername(String username) {
        if (!username.isEmpty()) {
            loginPage.enterUsername(username);
        }
    }

    @When("saya memasukkan password {string}")
    public void iEnterPassword(String password) {
        if (!password.isEmpty()) {
            loginPage.enterPassword(password);
        }
    }

    @When("saya klik tombol Login")
    public void iClickLoginButton() {
        loginPage.clickLoginButton();
        sleep(2000);
    }

    @Then("saya harus diarahkan keluar dari halaman login")
    public void iShouldBeRedirectedAwayFromLogin() {
        Assert.assertFalse(loginPage.isOnLoginPage(),
                "Seharusnya diarahkan keluar dari halaman login setelah login berhasil. URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("saya berada di halaman dashboard")
    public void iAmOnDashboardPage() {
        String url = DriverManager.getDriver().getCurrentUrl();
        Assert.assertFalse(url.contains("/login"),
                "Harus di halaman dashboard, bukan login. URL: " + url);
    }

    @Then("saya harus melihat pesan error atau tetap di halaman login")
    public void iShouldSeeErrorOrStayOnLogin() {
        boolean onLoginPage = loginPage.isOnLoginPage();
        boolean hasError = loginPage.isErrorToastDisplayed();
        Assert.assertTrue(onLoginPage || hasError,
                "Seharusnya tetap di login atau tampilkan error. URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("saya tetap berada di halaman login")
    public void iStayOnLoginPage() {
        sleep(1500);
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Seharusnya tetap di halaman login. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("saya melihat link menuju halaman register")
    public void iSeeRegisterLink() {
        Assert.assertTrue(loginPage.isDisplayed(locators.LoginPageLocators.REGISTER_LINK),
                "Link register seharusnya terlihat di halaman login");
    }

    @Then("saya melihat link lupa password")
    public void iSeeForgotPasswordLink() {
        Assert.assertTrue(loginPage.isDisplayed(locators.LoginPageLocators.FORGOT_PWD_LINK),
                "Link lupa password seharusnya terlihat di halaman login");
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
