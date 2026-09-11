package data;

import org.testng.annotations.DataProvider;

/**
 * TestNG DataProvider for ticket endpoint test scenarios.
 *
 * Supplies varied input rows to ticket-related @Test methods, enabling
 * data-driven testing without duplicating test logic.
 *
 * Each @DataProvider method returns Object[][] where:
 *   - Outer array = rows (one execution per row)
 *   - Inner array = columns (matched to @Test method parameters by position)
 */
public class TicketDataProvider {

    /**
     * Positive ticket creation scenarios — all should return HTTP 200.
     *
     * Columns: [title, description, isPublic, scenarioDescription]
     *
     * Covers:
     *   - Public ticket (visible to all users)
     *   - Private ticket (visible only to the creator)
     *   - Bug report with a descriptive title
     */
    @DataProvider(name = "createTicketData")
    public static Object[][] createTicketData() {
        return new Object[][] {
            { "Public Ticket Automation",    "Description for public ticket testing",           true,  "Create public ticket with full data" },
            { "Private Ticket Automation",   "Description for private ticket testing",           false, "Create private ticket (isPublic = false)" },
            { "Bug: Login page error",       "When logging in with valid credentials, error 500 appears", true, "Public bug report with technical description" },
        };
    }

    /**
     * Negative ticket creation scenarios — all should fail (not HTTP 200).
     *
     * Columns: [title, description, isPublic, scenarioDescription]
     *
     * API validation rule: title is required with minLength: 1
     */
    @DataProvider(name = "createTicketInvalidData")
    public static Object[][] createTicketInvalidData() {
        return new Object[][] {
            // Empty title violates minLength: 1
            { "", "Description present but title is empty", true, "Empty ticket title (minLength violation)" },
        };
    }

    /**
     * Order options for fetching active tickets.
     *
     * Columns: [order, scenarioDescription]
     *
     * The 'order' query parameter controls how the ticket list is sorted.
     * All three values are valid per the API docs.
     */
    @DataProvider(name = "activeTicketOrderData")
    public static Object[][] activeTicketOrderData() {
        return new Object[][] {
            { "VOTE",   "Sort tickets by vote count (most voted first)" },
            { "NEWEST", "Sort tickets by creation date (newest first)" },
            { "SOLVE",  "Sort tickets by solved status" },
        };
    }

    /**
     * Invalid ticket IDs for negative get-by-ID scenarios.
     *
     * Columns: [ticketId, scenarioDescription]
     */
    @DataProvider(name = "getTicketInvalidIdData")
    public static Object[][] getTicketInvalidIdData() {
        return new Object[][] {
            { "id-does-not-exist-12345",  "Non-existent ticket ID" },
            { "",                          "Empty ticket ID" },
            { "null",                      "String literal 'null' as ticket ID" },
        };
    }
}
