package tests.api.ticket;

import base.BaseApiTest;
import body.ticket.CreateTicketBody;
import data.TicketDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

/**
 * Test class for the Resonance ticket creation endpoint.
 * Uses a DataProvider for data-driven testing.
 */
public class CreateTicketTest extends BaseApiTest {

    /**
     * TC-TICKET-001: Creates a ticket using various input data (data-driven).
     * Data is supplied by TicketDataProvider.
     * The ticket ID from the first public row is saved for subsequent tests.
     */
    @Test(priority = 1,
          dataProvider = "createTicketData",
          dataProviderClass = TicketDataProvider.class,
          description = "Create a ticket with various data variations")
    public void testCreateTicketWithDataProvider(String title, String description, boolean isPublic, String scenario) {
        // Append a random string so the title is unique on every run
        String uniqueTitle = title + " - " + Utils.generateRandomString(4);
        System.out.println("[INFO] Scenario: " + scenario + " | Title: " + uniqueTitle);

        Response response = authRequest()
                .body(CreateTicketBody.build(uniqueTitle, description, isPublic).toString())
                .post("/api/rest/createTicket");

        // Validate status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Ticket creation must succeed for scenario: " + scenario);

        // Validate ticket ID is present in the response
        String ticketId = response.jsonPath().getString("id");
        Assert.assertNotNull(ticketId, "Ticket ID must be returned after the ticket is created");

        // Validate title matches what was sent
        Assert.assertEquals(response.jsonPath().getString("title"), uniqueTitle,
                "Ticket title must match the one sent");

        // Validate public/private status
        Assert.assertEquals(response.jsonPath().getBoolean("isPublic"), isPublic,
                "isPublic status must match the one sent");

        // Save the first public ticket ID for subsequent tests (comments, progressions, etc.)
        if (isPublic) {
            JsonFileManager.writeValue(TICKET_ID_FILE, "ticketId", ticketId);
        }

        System.out.println("[PASS] " + scenario + " | Ticket ID: " + ticketId);
    }

    /**
     * TC-TICKET-002: Creating a ticket without authentication must fail.
     */
    @Test(priority = 2, description = "Ticket creation without an authentication token must fail")
    public void testCreateTicketWithoutAuth() {
        String title = "Ticket Without Auth - " + Utils.generateRandomString(6);

        Response response = baseRequest()
                .body(CreateTicketBody.build(title, "Ticket description without auth", true).toString())
                .post("/api/rest/createTicket");

        // Without authentication the server must reject the request (401 or 403)
        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Ticket creation without a token must be rejected");

        System.out.println("[PASS] Request without auth rejected with status: " + response.getStatusCode());
    }
}
