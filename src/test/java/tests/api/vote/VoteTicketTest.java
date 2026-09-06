package tests.api.vote;

import base.BaseApiTest;
import body.vote.VoteBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;

public class VoteTicketTest extends BaseApiTest {

    @Test(priority = 1)
    public void testCheckUserVote() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .queryParam("ticketId", ticketId)
                .get("/api/rest/checkUserVote");

        Assert.assertEquals(response.getStatusCode(), 200, "Check user vote should return 200");
        Assert.assertNotNull(response.jsonPath().get("hasVoted"), "hasVoted field should exist");
        Assert.assertNotNull(response.jsonPath().get("voteCount"), "voteCount field should exist");
        System.out.println("Has voted: " + response.jsonPath().getBoolean("hasVoted") +
                " | Vote count: " + response.jsonPath().getInt("voteCount"));
    }

    @Test(priority = 2)
    public void testVoteTicket() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .body(VoteBody.build(ticketId).toString())
                .post("/api/rest/voteTicket");

        Assert.assertEquals(response.getStatusCode(), 200, "Vote ticket should return 200");
        Assert.assertNotNull(response.jsonPath().get("hasVoted"), "hasVoted field should exist");
        Assert.assertNotNull(response.jsonPath().get("voteCount"), "voteCount field should exist");
        System.out.println("After vote — has voted: " + response.jsonPath().getBoolean("hasVoted") +
                " | Count: " + response.jsonPath().getInt("voteCount"));
    }

    @Test(priority = 3)
    public void testUnvoteTicket() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .body(VoteBody.build(ticketId).toString())
                .post("/api/rest/voteTicket");

        Assert.assertEquals(response.getStatusCode(), 200, "Unvote (toggle) should return 200");
        System.out.println("After toggle — has voted: " + response.jsonPath().getBoolean("hasVoted"));
    }
}
