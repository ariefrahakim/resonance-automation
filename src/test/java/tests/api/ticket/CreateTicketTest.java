package tests.api.ticket;

import base.BaseApiTest;
import body.ticket.CreateTicketBody;
import data.TicketDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

/**
 * Kelas pengujian untuk endpoint pembuatan tiket Resonance.
 * Menggunakan DataProvider untuk pengujian berbasis data.
 */
public class CreateTicketTest extends BaseApiTest {

    /**
     * TC-TICKET-001: Membuat tiket menggunakan berbagai data input (data-driven).
     * Data disuplai dari TicketDataProvider.
     * ID tiket dari baris pertama disimpan untuk pengujian selanjutnya.
     */
    @Test(priority = 1,
          dataProvider = "createTicketData",
          dataProviderClass = TicketDataProvider.class,
          description = "Membuat tiket dengan berbagai variasi data")
    public void testCreateTicketWithDataProvider(String title, String description, boolean isPublic, String scenario) {
        // Tambahkan string acak agar judul unik setiap kali dijalankan
        String uniqueTitle = title + " - " + Utils.generateRandomString(4);
        System.out.println("[INFO] Skenario: " + scenario + " | Judul: " + uniqueTitle);

        Response response = authRequest()
                .body(CreateTicketBody.build(uniqueTitle, description, isPublic).toString())
                .post("/api/rest/createTicket");

        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Pembuatan tiket harus berhasil untuk skenario: " + scenario);

        // Validasi ID tiket ada di respons
        String ticketId = response.jsonPath().getString("id");
        Assert.assertNotNull(ticketId, "ID tiket harus dikembalikan setelah tiket dibuat");

        // Validasi judul sesuai yang dikirim
        Assert.assertEquals(response.jsonPath().getString("title"), uniqueTitle,
                "Judul tiket harus sesuai dengan yang dikirim");

        // Validasi status publik/privat
        Assert.assertEquals(response.jsonPath().getBoolean("isPublic"), isPublic,
                "Status isPublic harus sesuai dengan yang dikirim");

        // Simpan ID tiket pertama untuk pengujian berikutnya (komentar, progres, dll.)
        if (isPublic) {
            JsonFileManager.writeValue(TICKET_ID_FILE, "ticketId", ticketId);
        }

        System.out.println("[PASS] " + scenario + " | ID Tiket: " + ticketId);
    }

    /**
     * TC-TICKET-002: Membuat tiket tanpa autentikasi harus gagal.
     */
    @Test(priority = 2, description = "Pembuatan tiket tanpa token autentikasi harus gagal")
    public void testCreateTicketWithoutAuth() {
        String title = "Tiket Tanpa Auth - " + Utils.generateRandomString(6);

        Response response = baseRequest()
                .body(CreateTicketBody.build(title, "Deskripsi tiket tanpa auth", true).toString())
                .post("/api/rest/createTicket");

        // Tanpa autentikasi, server harus menolak permintaan (401 atau 403)
        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pembuatan tiket tanpa token harus ditolak");

        System.out.println("[PASS] Request tanpa auth ditolak dengan status: " + response.getStatusCode());
    }
}
