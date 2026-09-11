package data;

import org.testng.annotations.DataProvider;

/**
 * TestNG DataProvider for comment endpoint test scenarios.
 *
 * Provides varied comment content rows to the create-comment @Test method,
 * demonstrating data-driven testing across different comment types.
 */
public class CommentDataProvider {

    /**
     * Positive comment creation scenarios — all should return HTTP 200.
     *
     * Columns: [commentBody, scenarioDescription]
     *
     * API validation rule: body is required with minLength: 1
     */
    @DataProvider(name = "createCommentData")
    public static Object[][] createCommentData() {
        return new Object[][] {
            { "First comment from automation test",                        "Plain text comment" },
            { "Currently under investigation by the responsible team",    "Status update comment" },
            { "This issue already has an existing ticket, please merge",  "Duplicate reference comment" },
        };
    }

    /**
     * Negative comment creation scenarios — all should fail (not HTTP 200).
     *
     * Columns: [commentBody, ticketId, scenarioDescription]
     */
    @DataProvider(name = "createCommentInvalidData")
    public static Object[][] createCommentInvalidData() {
        return new Object[][] {
            // Empty body violates API minLength: 1
            { "",              "valid-ticket-id",        "Empty comment body (minLength violation)" },
            // Valid body but non-existent ticket
            { "Valid comment", "non-existent-ticket-xyz", "Valid body but ticket ID not found" },
        };
    }
}
