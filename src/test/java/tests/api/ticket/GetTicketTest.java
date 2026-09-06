package tests.api.ticket;

import base.BaseApiTest;
import data.TicketDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

/**
 * Kelas pengujian untuk endpoint pengambilan data tiket.
 * Mencakup: tiket aktif, tiket saya, tiket berdasarkan ID, dan jumlah tiket.
 */
public class GetTicketTest extends BaseApiTest {

    /**
     * TC-GET-001: Mengambil daftar tiket aktif dengan berbagai opsi pengurutan (data-driven).
     */
    @Test(priority = 1,
          dataProvider = "activeTicketOrderData",
          dataProviderClass = TicketDataProvider.class,
          description = "Mengambil tiket aktif dengan berbagai opsi urutan")
    public void testGetActiveTicketsWithOrder(String order, String scenario) {
        System.out.println("[INFO] Skenario: " + scenario + " | Order: " + order);

        Response response = authRequest()
                .queryParam("date", Utils.getCurrentDateTime())
                .queryParam("order", order)
                .queryParam("search", "")
                .get("/api/rest/activeTickets");

        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Pengambilan tiket aktif harus berhasil untuk order: " + order);

        // Validasi respons berupa array
        Assert.assertNotNull(response.jsonPath().getList("$"),
                "Respons harus berupa list tiket");

        System.out.println("[PASS] " + scenario + " | Jumlah tiket: " + response.jsonPath().getList("$").size());
    }

    /**
     * TC-GET-002: Mengambil daftar tiket milik pengguna yang sedang login.
     */
    @Test(priority = 2, description = "Mengambil tiket milik user yang sedang login")
    public void testGetMyTickets() {
        Response response = authRequest()
                .queryParam("limit", 5)
                .queryParam("page", 0)
                .queryParam("orderColumn", "createdAt")
                .queryParam("orderBy", "desc")
                .get("/api/rest/myTickets");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Pengambilan tiket saya harus berhasil");
        Assert.assertNotNull(response.jsonPath().getList("$"),
                "Respons harus berupa list");

        System.out.println("[PASS] Jumlah tiket saya: " + response.jsonPath().getList("$").size());
    }

    /**
     * TC-GET-003: Mengambil tiket berdasarkan ID yang tersimpan dari pengujian sebelumnya.
     */
    @Test(priority = 3, description = "Mengambil tiket berdasarkan ID spesifik")
    public void testGetTicketById() {
        // Baca ID tiket yang disimpan sebelumnya dari file JSON
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");
        System.out.println("[INFO] Mengambil tiket dengan ID: " + ticketId);

        Response response = authRequest()
                .queryParam("id", ticketId)
                .get("/api/rest/ticketById");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Pengambilan tiket berdasarkan ID harus berhasil");

        // Validasi ID tiket di respons sesuai dengan yang dicari
        Assert.assertEquals(response.jsonPath().getString("id"), ticketId,
                "ID tiket dalam respons harus sama dengan yang dicari");

        System.out.println("[PASS] Tiket ditemukan: " + response.jsonPath().getString("title"));
    }

    /**
     * TC-GET-004: Menghitung jumlah tiket aktif.
     */
    @Test(priority = 4, description = "Menghitung jumlah tiket aktif")
    public void testCountActiveTickets() {
        Response response = authRequest()
                .queryParam("date", Utils.getCurrentDateTime())
                .queryParam("search", "")
                .get("/api/rest/countActiveTickets");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Penghitungan tiket aktif harus berhasil");

        System.out.println("[PASS] Jumlah tiket aktif: " + response.asString());
    }

    /**
     * TC-GET-005: Pencarian tiket aktif dengan kata kunci tertentu.
     */
    @Test(priority = 5, description = "Mencari tiket aktif dengan kata kunci")
    public void testSearchActiveTickets() {
        Response response = authRequest()
                .queryParam("date", Utils.getCurrentDateTime())
                .queryParam("order", "NEWEST")
                .queryParam("search", "Otomatis")
                .get("/api/rest/activeTickets");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Pencarian tiket harus berhasil");

        System.out.println("[PASS] Hasil pencarian tiket 'Otomatis': " + response.jsonPath().getList("$").size() + " tiket");
    }
}
