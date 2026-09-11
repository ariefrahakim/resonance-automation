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
 * Negative test class for the ticket endpoint.
 * Verifies that the API rejects invalid input as specified in the API documentation.
 */
public class NegativeTicketTest extends BaseApiTest {

    /**
     * TC-NEG-TICKET-001: Creating a ticket with an empty title must fail.
     * API docs: title is required, minLength: 1
     */
    @Test(priority = 1,
          dataProvider = "createTicketInvalidData",
          dataProviderClass = TicketDataProvider.class,
          description = "Creating a ticket with invalid data must fail")
    public void testCreateTicketWithInvalidData(String title, String description, boolean isPublic, String scenario) {
        System.out.println("[INFO] Negative scenario: " + scenario);

        Response response = authRequest()
                .body(CreateTicketBody.build(title, description, isPublic).toString())
                .post("/api/rest/createTicket");

        // An empty title must be rejected
        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Ticket creation with '" + scenario + "' should fail");

        System.out.println("[PASS] " + scenario + " - Rejected with status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-002: Retrieving a ticket with an invalid ID must fail.
     */
    @Test(priority = 2,
          dataProvider = "getTicketInvalidIdData",
          dataProviderClass = TicketDataProvider.class,
          description = "Retrieving a ticket with an invalid ID must fail")
    public void testGetTicketWithInvalidId(String ticketId, String scenario) {
        System.out.println("[INFO] Negative scenario: " + scenario + " | ID: " + ticketId);

        Response response = authRequest()
                .queryParam("id", ticketId)
                .get("/api/rest/ticketById");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Retrieving a ticket with an invalid ID should fail: " + scenario);

        System.out.println("[PASS] " + scenario + " - Status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-003: Retrieving active tickets without the required 'date' parameter must fail.
     */
    @Test(priority = 3, description = "Retrieving active tickets without the date parameter must fail")
    public void testGetActiveTicketsWithoutDate() {
        Response response = authRequest()
                .queryParam("order", "NEWEST")
                .get("/api/rest/activeTickets");

        // The date parameter is required — omitting it must return an error
        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Retrieving active tickets without the 'date' parameter should fail");

        System.out.println("[PASS] Request without date rejected with status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-004: Updating the status of a non-existent ticket must fail.
     */
    @Test(priority = 4, description = "Updating the status of a ticket with an invalid ID must fail")
    public void testUpdateStatusWithInvalidTicketId() {
        Response response = authRequest()
                .body(UpdateTicketStatusBody.buildSolved("id-that-does-not-exist", Utils.getCurrentDateTime()).toString())
                .put("/api/rest/updateStatusTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Updating the status of a non-existent ticket should fail");

        System.out.println("[PASS] Update with invalid ID rejected with status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-005: Deleting a ticket without authentication must fail.
     */
    @Test(priority = 5, description = "Deleting a ticket without an authentication token must be rejected")
    public void testDeleteTicketWithoutAuth() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = baseRequest()
                .queryParam("ticketId", ticketId)
                .delete("/api/rest/deleteTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Deleting a ticket without auth should be rejected");

        System.out.println("[PASS] Delete without auth rejected with status: " + response.getStatusCode());
    }

    /**
     * TC-NEG-TICKET-006: Updating ticket status without authentication must fail.
     */
    @Test(priority = 6, description = "Updating ticket status without authentication must be rejected")
    public void testUpdateStatusWithoutAuth() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = baseRequest()
                .body(UpdateTicketStatusBody.buildSolved(ticketId, Utils.getCurrentDateTime()).toString())
                .put("/api/rest/updateStatusTicket");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Updating ticket status without auth should be rejected");

        System.out.println("[PASS] Status update without auth rejected with status: " + response.getStatusCode());
    }
}
