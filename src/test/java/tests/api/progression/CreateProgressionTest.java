package tests.api.progression;

import base.BaseApiTest;
import body.progression.CreateProgressionBody;
import data.ProgressionDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;

/**
 * Test class for the ticket progression endpoint.
 * Uses a DataProvider for data variation in tests.
 */
public class CreateProgressionTest extends BaseApiTest {

    /**
     * TC-PROGRESS-001: Creates a ticket progression with various data (data-driven).
     * The last progression ID is saved for update and delete tests.
     */
    @Test(priority = 1,
          dataProvider = "createProgressionData",
          dataProviderClass = ProgressionDataProvider.class,
          description = "Create a ticket progression with various data variations")
    public void testCreateProgressionWithDataProvider(String title, String description, String scenario) {
        // Read the ticket ID previously saved
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");
        System.out.println("[INFO] Scenario: " + scenario + " | Ticket ID: " + ticketId);

        Response response = authRequest()
                .body(CreateProgressionBody.build(ticketId, title, description).toString())
                .post("/api/rest/createProgression");

        // Validate status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Progression creation must succeed for scenario: " + scenario);

        // Validate progression ID is present in the response
        String progressionId = response.jsonPath().getString("id");
        Assert.assertNotNull(progressionId, "Progression ID must be returned after creation");

        // Validate title matches what was sent
        Assert.assertEquals(response.jsonPath().getString("title"), title,
                "Progression title must match the one sent");

        // Save the last progression ID for update/delete tests
        JsonFileManager.writeValue(PROGRESSION_ID_FILE, "progressionId", progressionId);

        System.out.println("[PASS] " + scenario + " | Progression ID: " + progressionId);
    }

    /**
     * TC-PROGRESS-002: Retrieves the list of progressions for a given ticket ID.
     */
    @Test(priority = 2, description = "Retrieve progressions by ticket ID")
    public void testGetProgressionsByTicketId() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .queryParam("ticketId", ticketId)
                .get("/api/rest/progressionsByTicketId");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Retrieving progressions by ticket ID must succeed");
        Assert.assertNotNull(response.jsonPath().getList("$"),
                "Response must be a list of progressions");

        System.out.println("[PASS] Progression count on ticket: " + response.jsonPath().getList("$").size());
    }
}
