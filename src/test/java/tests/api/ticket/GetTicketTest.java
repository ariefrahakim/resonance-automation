package tests.api.ticket;

import base.BaseApiTest;
import data.TicketDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

/**
 * Test class for ticket retrieval endpoints.
 * Covers: active tickets, my tickets, ticket by ID, and ticket count.
 */
public class GetTicketTest extends BaseApiTest {

    /**
     * TC-GET-001: Retrieves the list of active tickets with various sort options (data-driven).
     */
    @Test(priority = 1,
          dataProvider = "activeTicketOrderData",
          dataProviderClass = TicketDataProvider.class,
          description = "Retrieve active tickets with various sort options")
    public void testGetActiveTicketsWithOrder(String order, String scenario) {
        System.out.println("[INFO] Scenario: " + scenario + " | Order: " + order);

        Response response = authRequest()
                .queryParam("date", Utils.getCurrentDateTime())
                .queryParam("order", order)
                .queryParam("search", "")
                .get("/api/rest/activeTickets");

        // Validate status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Retrieving active tickets must succeed for order: " + order);

        // Validate response is an array
        Assert.assertNotNull(response.jsonPath().getList("$"),
                "Response must be a list of tickets");

        System.out.println("[PASS] " + scenario + " | Ticket count: " + response.jsonPath().getList("$").size());
    }

    /**
     * TC-GET-002: Retrieves the list of tickets belonging to the currently logged-in user.
     */
    @Test(priority = 2, description = "Retrieve tickets belonging to the logged-in user")
    public void testGetMyTickets() {
        Response response = authRequest()
                .queryParam("limit", 5)
                .queryParam("page", 0)
                .queryParam("orderColumn", "createdAt")
                .queryParam("orderBy", "desc")
                .get("/api/rest/myTickets");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Retrieving my tickets must succeed");
        Assert.assertNotNull(response.jsonPath().getList("$"),
                "Response must be a list");

        System.out.println("[PASS] My ticket count: " + response.jsonPath().getList("$").size());
    }

    /**
     * TC-GET-003: Retrieves a ticket by the ID saved from a previous test.
     */
    @Test(priority = 3, description = "Retrieve a ticket by a specific ID")
    public void testGetTicketById() {
        // Read the ticket ID previously saved to the JSON file
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");
        System.out.println("[INFO] Retrieving ticket with ID: " + ticketId);

        Response response = authRequest()
                .queryParam("id", ticketId)
                .get("/api/rest/ticketById");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Retrieving a ticket by ID must succeed");

        // Validate the ticket ID in the response matches the one requested
        Assert.assertEquals(response.jsonPath().getString("id"), ticketId,
                "Ticket ID in the response must match the requested ID");

        System.out.println("[PASS] Ticket found: " + response.jsonPath().getString("title"));
    }

    /**
     * TC-GET-004: Counts the number of active tickets.
     */
    @Test(priority = 4, description = "Count the number of active tickets")
    public void testCountActiveTickets() {
        Response response = authRequest()
                .queryParam("date", Utils.getCurrentDateTime())
                .queryParam("search", "")
                .get("/api/rest/countActiveTickets");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Counting active tickets must succeed");

        System.out.println("[PASS] Active ticket count: " + response.asString());
    }

    /**
     * TC-GET-005: Searches for active tickets using a specific keyword.
     */
    @Test(priority = 5, description = "Search for active tickets with a keyword")
    public void testSearchActiveTickets() {
        Response response = authRequest()
                .queryParam("date", Utils.getCurrentDateTime())
                .queryParam("order", "NEWEST")
                .queryParam("search", "Otomatis")
                .get("/api/rest/activeTickets");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Ticket search must succeed");

        System.out.println("[PASS] Search results for 'Otomatis': " + response.jsonPath().getList("$").size() + " tickets");
    }
}
