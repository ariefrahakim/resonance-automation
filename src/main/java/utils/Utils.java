package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Utils {
    private static final Random random = new Random();
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String generateRandomEmail() {
        return "test_" + generateRandomString(6) + "@resonance.test";
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(ISO_FMT);
    }

    public static String getDateTimeAfterDays(int days) {
        return LocalDateTime.now().plusDays(days).format(ISO_FMT);
    }
}
