package tests.api.comment;

import base.BaseApiTest;
import body.comment.CreateCommentBody;
import body.comment.UpdateCommentBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;

/**
 * Kelas pengujian negatif untuk endpoint komentar.
 * Memverifikasi bahwa API menolak input tidak valid sesuai dokumentasi.
 */
public class NegativeCommentTest extends BaseApiTest {

    /**
     * TC-NEG-COMMENT-001: Membuat komentar dengan isi kosong harus gagal.
     * API docs: body wajib dengan minLength: 1
     */
    @Test(priority = 1, description = "Membuat komentar dengan body kosong harus gagal")
    public void testCreateCommentWithEmptyBody() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");
        System.out.println("[INFO] Mencoba membuat komentar dengan body kosong pada tiket: " + ticketId);

        Response response = authRequest()
                .body(CreateCommentBody.build(ticketId, "").toString())
                .post("/api/rest/createComment");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pembuatan komentar dengan body kosong seharusnya gagal (minLength: 1)");

        System.out.println("[PASS] Komentar body kosong ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-COMMENT-002: Membuat komentar dengan ID tiket yang tidak ada.
     */
    @Test(priority = 2, description = "Membuat komentar pada tiket yang tidak ada harus gagal")
    public void testCreateCommentOnNonExistentTicket() {
        Response response = authRequest()
                .body(CreateCommentBody.build("id-tiket-palsu-xyz-123", "Komentar valid tapi ID tiket salah").toString())
                .post("/api/rest/createComment");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pembuatan komentar pada tiket yang tidak ada seharusnya gagal");

        System.out.println("[PASS] Komentar pada tiket tidak ada ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-COMMENT-003: Membuat komentar tanpa autentikasi harus gagal.
     */
    @Test(priority = 3, description = "Membuat komentar tanpa token autentikasi harus ditolak")
    public void testCreateCommentWithoutAuth() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = baseRequest()
                .body(CreateCommentBody.build(ticketId, "Komentar tanpa auth").toString())
                .post("/api/rest/createComment");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pembuatan komentar tanpa auth seharusnya ditolak");

        System.out.println("[PASS] Komentar tanpa auth ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-COMMENT-004: Update komentar dengan body kosong harus gagal.
     * API docs: body wajib dengan minLength: 1
     */
    @Test(priority = 4, description = "Update komentar dengan body kosong harus gagal")
    public void testUpdateCommentWithEmptyBody() {
        String commentId = JsonFileManager.readValue(COMMENT_ID_FILE, "commentId");

        Response response = authRequest()
                .body(UpdateCommentBody.build(commentId, "").toString())
                .put("/api/rest/updateComment");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Update komentar dengan body kosong seharusnya gagal (minLength: 1)");

        System.out.println("[PASS] Update komentar body kosong ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-COMMENT-005: Menghapus komentar dengan ID yang tidak ada.
     */
    @Test(priority = 5, description = "Menghapus komentar dengan ID tidak valid harus gagal")
    public void testDeleteNonExistentComment() {
        Response response = authRequest()
                .queryParam("id", "id-komentar-tidak-ada-xyz")
                .delete("/api/rest/deleteComment");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Penghapusan komentar yang tidak ada seharusnya gagal");

        System.out.println("[PASS] Delete komentar tidak ada ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-COMMENT-006: Mengambil komentar tanpa parameter ticketId harus gagal.
     */
    @Test(priority = 6, description = "Mengambil komentar tanpa ticketId harus gagal")
    public void testGetCommentsWithoutTicketId() {
        Response response = authRequest()
                .queryParam("limit", 5)
                .queryParam("page", 0)
                .get("/api/rest/commentsByTicketId");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Pengambilan komentar tanpa ticketId seharusnya gagal");

        System.out.println("[PASS] Request tanpa ticketId ditolak dengan status: " + response.getStatusCode());
    }
}
