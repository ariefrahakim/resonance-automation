package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.HistoryPage;
import pages.HomePage;
import utils.DriverManager;

/**
 * Step definitions untuk fitur riwayat tiket.
 */
public class HistorySteps {

    private final HomePage    homePage    = new HomePage(DriverManager.getDriver());
    private final HistoryPage historyPage = new HistoryPage(DriverManager.getDriver());

    @When("saya klik menu History di navbar")
    public void iClickHistoryInNavbar() {
        homePage.clickHistoryNav();
        sleep(1500);
    }

    @Then("saya berada di halaman riwayat tiket")
    public void iAmOnHistoryPage() {
        Assert.assertTrue(historyPage.isOnHistoryPage(),
                "Seharusnya berada di halaman /history. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("halaman riwayat berhasil dimuat")
    public void historyPageLoaded() {
        String url = DriverManager.getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("/history"),
                "Halaman riwayat seharusnya berhasil dimuat. URL: " + url);
    }

    @Then("konten halaman riwayat tidak kosong")
    public void historyPageContentNotEmpty() {
        String pageSource = DriverManager.getDriver().getPageSource();
        Assert.assertFalse(pageSource.isEmpty(), "Konten halaman riwayat seharusnya tidak kosong");
    }

    @When("saya klik tombol kembali ke Dashboard")
    public void iClickBackToDashboard() {
        historyPage.clickBackToDashboard();
        sleep(1500);
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
