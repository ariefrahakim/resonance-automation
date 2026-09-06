package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.HomePage;
import pages.NewTicketPage;
import utils.DriverManager;
import utils.Utils;

/**
 * Step definitions untuk fitur tiket (buat dan lihat tiket).
 */
public class TicketSteps {

    private final HomePage      homePage      = new HomePage(DriverManager.getDriver());
    private final NewTicketPage newTicketPage = new NewTicketPage(DriverManager.getDriver());

    @When("saya klik tombol Create Ticket")
    public void iClickCreateTicket() {
        homePage.clickCreateTicket();
        sleep(1500);
    }

    @Then("saya berada di halaman buat tiket baru")
    public void iAmOnNewTicketPage() {
        Assert.assertTrue(newTicketPage.isOnNewPage(),
                "Seharusnya berada di halaman /new. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @When("saya mengisi judul tiket dengan {string}")
    public void iEnterTicketTitle(String title) {
        // Tambahkan suffix acak agar tidak duplikat di setiap run
        String uniqueTitle = title + " - " + Utils.generateRandomString(4);
        newTicketPage.enterTitle(uniqueTitle);
    }

    @And("saya mengisi deskripsi tiket dengan {string}")
    public void iEnterTicketDescription(String description) {
        newTicketPage.enterDescription(description);
    }

    @And("saya memilih opsi Private")
    public void iSelectPrivateOption() {
        newTicketPage.selectPrivate();
    }

    @And("saya memilih opsi Public")
    public void iSelectPublicOption() {
        newTicketPage.selectPublic();
    }

    @When("saya klik Submit Ticket")
    public void iClickSubmitTicket() {
        newTicketPage.clickSubmit();
        sleep(2000);
    }

    @Then("tiket berhasil dibuat")
    public void ticketIsCreatedSuccessfully() {
        boolean redirected  = !newTicketPage.isOnNewPage();
        boolean hasSuccess  = newTicketPage.isSuccessToastDisplayed();
        Assert.assertTrue(redirected || hasSuccess,
                "Tiket seharusnya berhasil dibuat (redirect atau toast sukses). URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("tiket gagal dibuat dan saya tetap di halaman buat tiket")
    public void ticketCreationFailsAndStaysOnPage() {
        sleep(1000);
        boolean stayedOnPage = newTicketPage.isOnNewPage();
        boolean hasError     = newTicketPage.isErrorToastDisplayed();
        Assert.assertTrue(stayedOnPage || hasError,
                "Tiket dengan judul kosong seharusnya gagal dibuat. URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("halaman dashboard berhasil dimuat")
    public void dashboardLoaded() {
        String url = DriverManager.getDriver().getCurrentUrl();
        Assert.assertFalse(url.contains("/login"),
                "Halaman dashboard seharusnya berhasil dimuat. URL: " + url);
    }

    @When("saya memfilter tiket dengan urutan {string}")
    public void iFilterByOrder(String order) {
        switch (order.toLowerCase()) {
            case "vote":
                homePage.filterByVote();
                break;
            case "newest":
                homePage.filterByNewest();
                break;
            default:
                throw new IllegalArgumentException("Order tidak dikenali: " + order);
        }
        sleep(1000);
    }

    @When("saya mencari tiket dengan kata kunci {string}")
    public void iSearchTicket(String keyword) {
        homePage.searchTicket(keyword);
        sleep(1000);
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
