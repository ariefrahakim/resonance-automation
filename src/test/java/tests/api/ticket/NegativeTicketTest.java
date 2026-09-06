package tests.api.ticket;

import base.BaseApiTest;
import body.ticket.CreateTicketBody;
import body.ticket.UpdateTicketStatusBody;
import data.TicketDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

/**
 * Kelas pengujian negatif untuk endpoint tiket.
 * Memverifikasi bahwa API menolak input tidak valid sesuai dokumentasi.
 */
public class NegativeTicketTest extends BaseApiTest {

    /**
     * TC-NEG-TICKET-001: Membuat tiket dengan judul kosong harus gagal.
     * API docs: title wajib, minLength: 1
     */
    @Test(priority = 1,
          dataProvider = "createTicketInvalidData",
          dataProviderClass = TicketDataProvider.class,
          description = "Membuat tiket dengan data tidak valid harus gagal")
    public void testCreateTicketWithInvalidData(String title, String description, boolean isPublic, String scenario) {
        System.out.println("[INFO] Skenario negatif: " + scenario);

        Response response = authRequest()
                .body(CreateTicketBody.build(title, description, isPublic).toString())
                .post("/api/rest/createTicket");

        // Judul kosong seharusnya ditolak
        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pembuatan tiket dengan '" + scenario + "' seharusnya gagal");

        System.out.println("[PASS] " + scenario + " - Ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-002: Mengambil tiket dengan ID yang tidak valid harus gagal.
     */
    @Test(priority = 2,
          dataProvider = "getTicketInvalidIdData",
          dataProviderClass = TicketDataProvider.class,
          description = "Mengambil tiket dengan ID tidak valid harus gagal")
    public void testGetTicketWithInvalidId(String ticketId, String scenario) {
        System.out.println("[INFO] Skenario negatif: " + scenario + " | ID: " + ticketId);

        Response response = authRequest()
                .queryParam("id", ticketId)
                .get("/api/rest/ticketById");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pengambilan tiket dengan ID tidak valid seharusnya gagal: " + scenario);

        System.out.println("[PASS] " + scenario + " - Status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-003: Mengambil tiket aktif tanpa parameter 'date' yang wajib.
     */
    @Test(priority = 3, description = "Mengambil tiket aktif tanpa parameter date harus gagal")
    public void testGetActiveTicketsWithoutDate() {
        Response response = authRequest()
                .queryParam("order", "NEWEST")
                .get("/api/rest/activeTickets");

        // Parameter date wajib - tanpanya seharusnya error
        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pengambilan tiket aktif tanpa parameter 'date' seharusnya gagal");

        System.out.println("[PASS] Request tanpa date ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-004: Update status tiket dengan ID yang tidak ada.
     */
    @Test(priority = 4, description = "Update status tiket dengan ID tidak valid harus gagal")
    public void testUpdateStatusWithInvalidTicketId() {
        Response response = authRequest()
                .body(UpdateTicketStatusBody.buildSolved("id-tidak-ada-sama-sekali", Utils.getCurrentDateTime()).toString())
                .put("/api/rest/updateStatusTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Update status tiket yang tidak ada seharusnya gagal");

        System.out.println("[PASS] Update ID tidak valid ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-005: Menghapus tiket tanpa autentikasi harus gagal.
     */
    @Test(priority = 5, description = "Menghapus tiket tanpa token autentikasi harus ditolak")
    public void testDeleteTicketWithoutAuth() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = baseRequest()
                .queryParam("ticketId", ticketId)
                .delete("/api/rest/deleteTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Penghapusan tiket tanpa auth seharusnya ditolak");

        System.out.println("[PASS] Delete tanpa auth ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-006: Update status tiket tanpa autentikasi harus gagal.
     */
    @Test(priority = 6, description = "Update status tiket tanpa autentikasi harus ditolak")
    public void testUpdateStatusWithoutAuth() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = baseRequest()
                .body(UpdateTicketStatusBody.buildSolved(ticketId, Utils.getCurrentDateTime()).toString())
                .put("/api/rest/updateStatusTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Update status tiket tanpa auth seharusnya ditolak");

        System.out.println("[PASS] Update status tanpa auth ditolak dengan status: " + response.getStatusCode());
    }
}
