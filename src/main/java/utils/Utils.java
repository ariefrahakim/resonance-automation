package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * General-purpose utility methods used across API and Web tests.
 *
 * Provides:
 *   - Random string / email generation for unique test data
 *   - ISO-8601 timestamp formatting for date-based API parameters
 */
public class Utils {

    private static final Random random = new Random();

    /** ISO-8601 format expected by the Resonance API for datetime fields. */
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * Used to make test data unique across runs so tests are idempotent
     * (e.g., ticket titles, comment bodies).
     *
     * @param length number of characters to generate
     * @return a lowercase alphanumeric string
     */
    public static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generates a random email address for use in registration or negative login tests.
     *
     * Format: test_<6 random chars>@resonance.test
     *
     * @return a unique, deterministically unpredictable email address
     */
    public static String generateRandomEmail() {
        return "test_" + generateRandomString(6) + "@resonance.test";
    }

    /**
     * Returns the current local date-time formatted as ISO-8601.
     *
     * Used as the 'date' query parameter in activeTickets and countActiveTickets,
     * and as the 'solvedAt' value when marking a ticket as solved.
     *
     * @return current datetime string, e.g. "2026-09-11T16:00:00.000Z"
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(ISO_FMT);
    }

    /**
     * Returns a future date-time offset by the given number of days from now.
     *
     * Useful for setting 'expectedSolveAt' fields in ticket creation tests.
     *
     * @param days number of days to add to the current date
     * @return future datetime string in ISO-8601 format
     */
    public static String getDateTimeAfterDays(int days) {
        return LocalDateTime.now().plusDays(days).format(ISO_FMT);
    }
}
