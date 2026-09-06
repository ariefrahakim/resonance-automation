package tests.api.progression;

import base.BaseApiTest;
import body.progression.CreateProgressionBody;
import data.ProgressionDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;

/**
 * Kelas pengujian untuk endpoint progres tiket.
 * Menggunakan DataProvider untuk variasi data pengujian.
 */
public class CreateProgressionTest extends BaseApiTest {

    /**
     * TC-PROGRESS-001: Membuat progres tiket dengan berbagai data (data-driven).
     * ID progres terakhir disimpan untuk pengujian update dan delete.
     */
    @Test(priority = 1,
          dataProvider = "createProgressionData",
          dataProviderClass = ProgressionDataProvider.class,
          description = "Membuat progres tiket dengan berbagai variasi data")
    public void testCreateProgressionWithDataProvider(String title, String description, String scenario) {
        // Baca ID tiket yang sudah tersimpan
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");
        System.out.println("[INFO] Skenario: " + scenario + " | Tiket ID: " + ticketId);

        Response response = authRequest()
                .body(CreateProgressionBody.build(ticketId, title, description).toString())
                .post("/api/rest/createProgression");

        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Pembuatan progres harus berhasil untuk skenario: " + scenario);

        // Validasi ID progres ada di respons
        String progressionId = response.jsonPath().getString("id");
        Assert.assertNotNull(progressionId, "ID progres harus dikembalikan setelah dibuat");

        // Validasi judul sesuai yang dikirim
        Assert.assertEquals(response.jsonPath().getString("title"), title,
                "Judul progres harus sesuai yang dikirim");

        // Simpan ID progres terakhir untuk pengujian update/delete
        JsonFileManager.writeValue(PROGRESSION_ID_FILE, "progressionId", progressionId);

        System.out.println("[PASS] " + scenario + " | ID Progres: " + progressionId);
    }

    /**
     * TC-PROGRESS-002: Mengambil daftar progres berdasarkan ID tiket.
     */
    @Test(priority = 2, description = "Mengambil progres berdasarkan ID tiket")
    public void testGetProgressionsByTicketId() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .queryParam("ticketId", ticketId)
                .get("/api/rest/progressionsByTicketId");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Pengambilan progres berdasarkan tiket ID harus berhasil");
        Assert.assertNotNull(response.jsonPath().getList("$"),
                "Respons harus berupa list progres");

        System.out.println("[PASS] Jumlah progres pada tiket: " + response.jsonPath().getList("$").size());
    }
}
