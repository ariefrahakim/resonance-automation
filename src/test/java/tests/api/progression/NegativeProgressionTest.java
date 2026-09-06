package tests.api.progression;

import base.BaseApiTest;
import body.progression.CreateProgressionBody;
import body.progression.UpdateProgressionBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;

/**
 * Kelas pengujian negatif untuk endpoint progres tiket.
 * Memverifikasi bahwa API menolak input tidak valid sesuai dokumentasi.
 */
public class NegativeProgressionTest extends BaseApiTest {

    /**
     * TC-NEG-PROG-001: Membuat progres dengan judul kosong harus gagal.
     * API docs: title wajib dengan minLength: 1
     */
    @Test(priority = 1, description = "Membuat progres dengan judul kosong harus gagal")
    public void testCreateProgressionWithEmptyTitle() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .body(CreateProgressionBody.build(ticketId, "", "Deskripsi ada tapi judul kosong").toString())
                .post("/api/rest/createProgression");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pembuatan progres dengan judul kosong seharusnya gagal (minLength: 1)");

        System.out.println("[PASS] Progres judul kosong ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-PROG-002: Membuat progres pada tiket yang tidak ada harus gagal.
     */
    @Test(priority = 2, description = "Membuat progres pada tiket yang tidak ada harus gagal")
    public void testCreateProgressionOnNonExistentTicket() {
        Response response = authRequest()
                .body(CreateProgressionBody.build("id-tiket-tidak-ada-xyz", "Judul progres valid", "Deskripsi valid").toString())
                .post("/api/rest/createProgression");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pembuatan progres pada tiket yang tidak ada seharusnya gagal");

        System.out.println("[PASS] Progres pada tiket tidak ada ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-PROG-003: Membuat progres tanpa autentikasi harus gagal.
     */
    @Test(priority = 3, description = "Membuat progres tanpa token autentikasi harus ditolak")
    public void testCreateProgressionWithoutAuth() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = baseRequest()
                .body(CreateProgressionBody.build(ticketId, "Progres tanpa auth", "Deskripsi").toString())
                .post("/api/rest/createProgression");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pembuatan progres tanpa auth seharusnya ditolak");

        System.out.println("[PASS] Progres tanpa auth ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-PROG-004: Update progres dengan judul kosong harus gagal.
     * API docs: title wajib dengan minLength: 1
     */
    @Test(priority = 4, description = "Update progres dengan judul kosong harus gagal")
    public void testUpdateProgressionWithEmptyTitle() {
        String progressionId = JsonFileManager.readValue(PROGRESSION_ID_FILE, "progressionId");

        Response response = authRequest()
                .body(UpdateProgressionBody.build(progressionId, "", "Deskripsi ada tapi judul kosong").toString())
                .put("/api/rest/updateProgression");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Update progres dengan judul kosong seharusnya gagal (minLength: 1)");

        System.out.println("[PASS] Update progres judul kosong ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-PROG-005: Menghapus progres dengan ID yang tidak ada harus gagal.
     */
    @Test(priority = 5, description = "Menghapus progres dengan ID tidak valid harus gagal")
    public void testDeleteNonExistentProgression() {
        Response response = authRequest()
                .queryParam("id", "id-progres-tidak-ada-xyz")
                .delete("/api/rest/deleteProgression");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Penghapusan progres yang tidak ada seharusnya gagal");

        System.out.println("[PASS] Delete progres tidak ada ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-PROG-006: Mengambil progres tanpa parameter ticketId wajib harus gagal.
     */
    @Test(priority = 6, description = "Mengambil progres tanpa ticketId harus gagal")
    public void testGetProgressionsWithoutTicketId() {
        Response response = authRequest()
                .get("/api/rest/progressionsByTicketId");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pengambilan progres tanpa ticketId seharusnya gagal");

        System.out.println("[PASS] Request tanpa ticketId ditolak dengan status: " + response.getStatusCode());
    }
}
