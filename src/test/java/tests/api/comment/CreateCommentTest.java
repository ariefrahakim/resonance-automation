package tests.api.comment;

import base.BaseApiTest;
import body.comment.CreateCommentBody;
import data.CommentDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

/**
 * Kelas pengujian untuk endpoint komentar tiket.
 * Mencakup pembuatan komentar dan pengambilan komentar berdasarkan tiket.
 */
public class CreateCommentTest extends BaseApiTest {

    /**
     * TC-COMMENT-001: Membuat komentar dengan berbagai variasi isi (data-driven).
     * ID komentar terakhir disimpan untuk pengujian update dan delete.
     */
    @Test(priority = 1,
          dataProvider = "createCommentData",
          dataProviderClass = CommentDataProvider.class,
          description = "Membuat komentar dengan berbagai variasi konten")
    public void testCreateCommentWithDataProvider(String commentBody, String scenario) {
        // Baca ID tiket yang sudah disimpan sebelumnya
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");
        System.out.println("[INFO] Skenario: " + scenario + " | Tiket ID: " + ticketId);

        // Tambahkan tanda unik agar konten berbeda setiap run
        String uniqueComment = commentBody + " - " + Utils.generateRandomString(4);

        Response response = authRequest()
                .body(CreateCommentBody.build(ticketId, uniqueComment).toString())
                .post("/api/rest/createComment");

        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Pembuatan komentar harus berhasil untuk skenario: " + scenario);

        // Validasi ID komentar ada di respons
        String commentId = response.jsonPath().getString("id");
        Assert.assertNotNull(commentId, "ID komentar harus dikembalikan setelah komentar dibuat");

        // Validasi tiket ID sesuai
        Assert.assertEquals(response.jsonPath().getString("ticketId"), ticketId,
                "ID tiket di respons harus sesuai");

        // Simpan ID komentar terakhir untuk pengujian update/delete
        JsonFileManager.writeValue(COMMENT_ID_FILE, "commentId", commentId);

        System.out.println("[PASS] " + scenario + " | ID Komentar: " + commentId);
    }

    /**
     * TC-COMMENT-002: Mengambil daftar komentar berdasarkan ID tiket.
     */
    @Test(priority = 2, description = "Mengambil komentar berdasarkan ID tiket")
    public void testGetCommentsByTicketId() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .queryParam("ticketId", ticketId)
                .queryParam("limit", 10)
                .queryParam("page", 0)
                .get("/api/rest/commentsByTicketId");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Pengambilan komentar berdasarkan tiket ID harus berhasil");
        Assert.assertNotNull(response.jsonPath().getList("$"),
                "Respons harus berupa list komentar");

        System.out.println("[PASS] Jumlah komentar pada tiket: " + response.jsonPath().getList("$").size());
    }
}
