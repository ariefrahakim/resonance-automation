package tests.api.ticket;

import base.BaseApiTest;
import body.ticket.CreateTicketBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Utils;

public class DeleteTicketTest extends BaseApiTest {

    @Test(priority = 1)
    public void testDeleteTicket() {
        String title = "To Delete " + Utils.generateRandomString(6);
        Response createResponse = authRequest()
                .body(CreateTicketBody.build(title, "Will be deleted", true).toString())
                .post("/api/rest/createTicket");

        Assert.assertEquals(createResponse.getStatusCode(), 200, "Create ticket for deletion should succeed");
        String ticketId = createResponse.jsonPath().getString("id");

        Response deleteResponse = authRequest()
                .queryParam("ticketId", ticketId)
                .delete("/api/rest/deleteTicket");

        Assert.assertEquals(deleteResponse.getStatusCode(), 200, "Delete should return 200");
        Assert.assertTrue(deleteResponse.jsonPath().getBoolean("success"), "Delete should succeed");
        System.out.println("Deleted ticket: " + ticketId);
    }

    @Test(priority = 2)
    public void testDeleteNonExistentTicket() {
        Response response = authRequest()
                .queryParam("ticketId", "non-existent-ticket-id-12345")
                .delete("/api/rest/deleteTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200, "Deleting non-existent ticket should fail");
        System.out.println("Non-existent delete response: " + response.getStatusCode());
    }
}
