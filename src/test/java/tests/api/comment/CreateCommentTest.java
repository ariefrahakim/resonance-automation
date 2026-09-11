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
 * Test class for the ticket comment endpoint.
 * Covers comment creation and retrieval of comments by ticket.
 */
public class CreateCommentTest extends BaseApiTest {

    /**
     * TC-COMMENT-001: Creates a comment with various content variations (data-driven).
     * The last created comment ID is saved for update and delete tests.
     */
    @Test(priority = 1,
          dataProvider = "createCommentData",
          dataProviderClass = CommentDataProvider.class,
          description = "Create a comment with various content variations")
    public void testCreateCommentWithDataProvider(String commentBody, String scenario) {
        // Read the ticket ID saved by a previous test
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");
        System.out.println("[INFO] Scenario: " + scenario + " | Ticket ID: " + ticketId);

        // Append a unique tag so the content differs on every run
        String uniqueComment = commentBody + " - " + Utils.generateRandomString(4);

        Response response = authRequest()
                .body(CreateCommentBody.build(ticketId, uniqueComment).toString())
                .post("/api/rest/createComment");

        // Validate status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Comment creation must succeed for scenario: " + scenario);

        // Validate comment ID is present in the response
        String commentId = response.jsonPath().getString("id");
        Assert.assertNotNull(commentId, "Comment ID must be returned after the comment is created");

        // Validate ticket ID matches
        Assert.assertEquals(response.jsonPath().getString("ticketId"), ticketId,
                "Ticket ID in the response must match the one sent");

        // Save the last comment ID for update/delete tests
        JsonFileManager.writeValue(COMMENT_ID_FILE, "commentId", commentId);

        System.out.println("[PASS] " + scenario + " | Comment ID: " + commentId);
    }

    /**
     * TC-COMMENT-002: Retrieves the list of comments for a given ticket ID.
     */
    @Test(priority = 2, description = "Retrieve comments by ticket ID")
    public void testGetCommentsByTicketId() {
        String ticketId = JsonFileManager.readValue(TICKET_ID_FILE, "ticketId");

        Response response = authRequest()
                .queryParam("ticketId", ticketId)
                .queryParam("limit", 10)
                .queryParam("page", 0)
                .get("/api/rest/commentsByTicketId");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Retrieving comments by ticket ID must succeed");
        Assert.assertNotNull(response.jsonPath().getList("$"),
                "Response must be a list of comments");

        System.out.println("[PASS] Number of comments on ticket: " + response.jsonPath().getList("$").size());
    }
}
