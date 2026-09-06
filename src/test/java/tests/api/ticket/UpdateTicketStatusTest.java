package tests.api.ticket;

import base.BaseApiTest;
import body.ticket.UpdateTicketStatusBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

public class UpdateTicketStatusTest extends BaseApiTest {

    @Test(priority = 1)
    public void testMarkTicketAsSolved() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .body(UpdateTicketStatusBody.buildSolved(ticketId, Utils.getCurrentDateTime()).toString())
                .put("/api/rest/updateStatusTicket");

        Assert.assertEquals(response.getStatusCode(), 200, "Mark as solved should return 200");
        Assert.assertNotNull(response.jsonPath().get("solvedAt"), "solvedAt should be set after marking solved");
        System.out.println("Ticket marked solved at: " + response.jsonPath().getString("solvedAt"));
    }

    @Test(priority = 2)
    public void testMarkTicketAsUnsolved() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .body(UpdateTicketStatusBody.buildUnsolved(ticketId).toString())
                .put("/api/rest/updateStatusTicket");

        Assert.assertEquals(response.getStatusCode(), 200, "Mark as unsolved should return 200");
        System.out.println("Ticket marked as unsolved. solvedAt: " + response.jsonPath().get("solvedAt"));
    }
}
