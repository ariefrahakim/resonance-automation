package tests.api.vote;

import base.BaseApiTest;
import body.vote.VoteBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Kelas pengujian negatif untuk endpoint vote tiket.
 * Memverifikasi bahwa API menolak request tidak valid.
 */
public class NegativeVoteTest extends BaseApiTest {

    /**
     * TC-NEG-VOTE-001: Cek vote tanpa parameter ticketId wajib harus gagal.
     */
    @Test(priority = 1, description = "Cek vote tanpa ticketId harus gagal")
    public void testCheckVoteWithoutTicketId() {
        Response response = authRequest()
                .get("/api/rest/checkUserVote");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Cek vote tanpa ticketId seharusnya gagal");

        System.out.println("[PASS] checkUserVote tanpa ticketId ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-VOTE-002: Vote pada tiket yang tidak ada harus gagal.
     */
    @Test(priority = 2, description = "Vote pada tiket yang tidak ada harus gagal")
    public void testVoteNonExistentTicket() {
        Response response = authRequest()
                .body(VoteBody.build("id-tiket-tidak-ada-xyz").toString())
                .post("/api/rest/voteTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Vote pada tiket yang tidak ada seharusnya gagal");

        System.out.println("[PASS] Vote tiket tidak ada ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-VOTE-003: Vote tanpa autentikasi harus gagal.
     */
    @Test(priority = 3, description = "Vote tanpa token autentikasi harus ditolak")
    public void testVoteWithoutAuth() {
        Response response = baseRequest()
                .body(VoteBody.build("any-ticket-id").toString())
                .post("/api/rest/voteTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Vote tanpa auth seharusnya ditolak");

        System.out.println("[PASS] Vote tanpa auth ditolak dengan status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-VOTE-004: Cek vote pada tiket yang tidak ada harus gagal.
     */
    /**
     * TC-NEG-VOTE-004: Cek vote pada tiket yang tidak ada mengembalikan hasVoted=false.
     * API selalu return 200 untuk checkUserVote, tapi data menunjukkan tidak ada vote.
     */
    @Test(priority = 4, description = "Cek vote pada tiket tidak ada mengembalikan hasVoted false")
    public void testCheckVoteOnNonExistentTicket() {
        Response response = authRequest()
                .queryParam("ticketId", "id-tiket-tidak-ada-xyz")
                .get("/api/rest/checkUserVote");

        Assert.assertEquals(response.getStatusCode(), 200,
                "checkUserVote harus return 200 meski tiket tidak ada");
        Assert.assertFalse(response.jsonPath().getBoolean("hasVoted"),
                "hasVoted harus false untuk tiket yang tidak ada");

        System.out.println("[PASS] checkUserVote tiket tidak ada: hasVoted=" + response.jsonPath().getBoolean("hasVoted"));
    }
}
