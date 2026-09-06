package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.HistoryPage;
import pages.HomePage;
import pages.LoginPage;
import pages.NewTicketPage;
import utils.DriverManager;

/**
 * Step definitions umum yang digunakan di berbagai feature file.
 * Mencakup: login, navigasi, dan langkah-langkah bersama.
 */
public class CommonSteps {

    private final LoginPage    loginPage    = new LoginPage(DriverManager.getDriver());
    private final HomePage     homePage     = new HomePage(DriverManager.getDriver());
    private final NewTicketPage newTicketPage = new NewTicketPage(DriverManager.getDriver());
    private final HistoryPage  historyPage  = new HistoryPage(DriverManager.getDriver());

    @Given("saya sudah login sebagai {string} dengan password {string}")
    public void iAmLoggedInAs(String username, String password) {
        loginPage.login(username, password);
        sleep(2000);
        Assert.assertFalse(loginPage.isOnLoginPage(),
                "Login gagal — masih di halaman login. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Given("saya tidak dalam keadaan login")
    public void iAmNotLoggedIn() {
        // Kosongkan cookies agar tidak ada sesi aktif
        DriverManager.getDriver().manage().deleteAllCookies();
    }

    @Given("saya berada di halaman riwayat tiket")
    public void iAmOnHistoryPage() {
        historyPage.navigate();
        sleep(1500);
        Assert.assertTrue(historyPage.isOnHistoryPage(),
                "Seharusnya berada di halaman riwayat. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @When("saya berada di halaman dashboard")
    public void iNavigateToDashboard() {
        homePage.navigate();
        sleep(1500);
    }

    @When("saya mengakses halaman buat tiket secara langsung")
    public void iAccessNewTicketDirectly() {
        newTicketPage.navigate();
        sleep(2000);
    }

    @When("saya mengakses halaman riwayat secara langsung")
    public void iAccessHistoryDirectly() {
        historyPage.navigate();
        sleep(2000);
    }

    @When("saya mengakses halaman tiket dengan ID {string}")
    public void iAccessTicketById(String ticketId) {
        DriverManager.getDriver().get(utils.ConfigReader.getProperty("webUrl") + "/ticket/" + ticketId);
        sleep(2000);
    }

    @Then("saya diarahkan ke halaman login")
    public void iAmRedirectedToLogin() {
        String url = DriverManager.getDriver().getCurrentUrl();
        String pageSource = DriverManager.getDriver().getPageSource();
        boolean onLogin = url.contains("/login") || pageSource.contains("btn-login");
        Assert.assertTrue(onLogin,
                "Seharusnya diarahkan ke halaman login. URL: " + url);
    }

    @Then("halaman menampilkan error atau melakukan redirect")
    public void pageShowsErrorOrRedirects() {
        String pageSource = DriverManager.getDriver().getPageSource();
        String url = DriverManager.getDriver().getCurrentUrl();
        boolean hasError = pageSource.contains("404") || pageSource.contains("not found")
                || pageSource.contains("Error") || pageSource.contains("error");
        boolean redirected = !url.contains("id-tidak-valid-xyz-123");
        Assert.assertTrue(hasError || redirected,
                "Seharusnya menampilkan error atau redirect. URL: " + url);
    }

    @Then("konten halaman tidak kosong")
    public void pageContentIsNotEmpty() {
        String pageSource = DriverManager.getDriver().getPageSource();
        Assert.assertFalse(pageSource.isEmpty(), "Konten halaman seharusnya tidak kosong");
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
