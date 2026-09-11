package data;

import org.testng.annotations.DataProvider;

/**
 * TestNG DataProvider for ticket progression (progress update) test scenarios.
 *
 * Progressions represent the stages a ticket goes through from submission
 * to resolution. This DataProvider covers the three common QA workflow phases.
 */
public class ProgressionDataProvider {

    /**
     * Positive progression creation scenarios — all should return HTTP 200.
     *
     * Columns: [title, description, scenarioDescription]
     *
     * Models a realistic 3-phase ticket lifecycle:
     *   1. Investigation — team identifies the root cause
     *   2. Development   — solution is being built
     *   3. QA & Verify   — QA team validates the fix
     */
    @DataProvider(name = "createProgressionData")
    public static Object[][] createProgressionData() {
        return new Object[][] {
            { "Initial Investigation", "Team is identifying the root cause of the issue",   "Progression: investigation phase" },
            { "Solution Development",  "Development team is building the fix",               "Progression: development phase" },
            { "Testing & Verification","QA team is running tests to verify the solution",    "Progression: QA verification phase" },
        };
    }
}
