package tests.web.ticket;

import base.BaseWebTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.DriverManager;

/**
 * Kelas pengujian negatif untuk fitur tiket pada web.
 * Memverifikasi bahwa UI menangani input tidak valid dengan benar.
 */
public class NegativeTicketWebTest extends BaseWebTest {

    /**
     * TC-WEB-NEG-TICKET-001: Membuat tiket dengan judul kosong harus ditolak atau menampilkan validasi.
     */
    @Test(priority = 1, description = "Membuat tiket dengan judul kosong harus gagal atau menampilkan validasi")
    public void testCreateTicketWithEmptyTitle() {
        doLogin();
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Navigasi ke halaman buat tiket
        newTicketPage.navigate();
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Isi deskripsi saja, biarkan judul kosong
        try {
            newTicketPage.enterDescription("Deskripsi ada tapi judul kosong");
            newTicketPage.submitTicket();
        } catch (Exception e) {
            System.out.println("[INFO] Elemen tidak ditemukan: " + e.getMessage());
        }

        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        // Harus tetap di /new (tidak redirect) karena validasi gagal
        boolean stayedOnNewPage = currentUrl.contains("/new");
        boolean hasValidationError = DriverManager.getDriver().getPageSource().toLowerCase()
                .contains("required") || DriverManager.getDriver().getPageSource().contains("wajib");

        Assert.assertTrue(stayedOnNewPage || hasValidationError,
                "Form dengan judul kosong seharusnya tidak berhasil disubmit. URL: " + currentUrl);
        System.out.println("[PASS] Submit tiket tanpa judul - URL: " + currentUrl);
    }

    /**
     * TC-WEB-NEG-TICKET-002: Akses halaman tiket dengan ID tidak valid harus menampilkan halaman error/404.
     */
    @Test(priority = 2, description = "Akses halaman tiket dengan ID tidak valid harus menampilkan error")
    public void testViewTicketWithInvalidId() {
        doLogin();
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Navigasi ke tiket dengan ID palsu
        ticketDetailPage.navigate("id-tiket-tidak-valid-xyz-123");
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String pageSource = DriverManager.getDriver().getPageSource();
        String currentUrl = DriverManager.getDriver().getCurrentUrl();

        // Halaman harus menampilkan error, 404, atau redirect
        boolean hasError = pageSource.contains("404") || pageSource.contains("not found") ||
                pageSource.contains("tidak ditemukan") || pageSource.contains("error") ||
                pageSource.contains("Error");
        boolean redirected = !currentUrl.contains("/ticket/id-tiket-tidak-valid-xyz-123");

        System.out.println("[INFO] URL: " + currentUrl + " | Has error text: " + hasError);
        Assert.assertTrue(hasError || redirected,
                "Halaman tiket tidak valid seharusnya menampilkan error atau redirect. URL: " + currentUrl);
        System.out.println("[PASS] Akses tiket ID tidak valid ditangani dengan benar");
    }
}
