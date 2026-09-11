package utils;

import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for reading from and writing to JSON files.
 *
 * Used for sharing state between test classes (e.g., saving a Bearer token
 * after login so subsequent tests can use it without logging in again).
 *
 * Files are stored under src/main/resources/json/:
 *   token.json        — Bearer token from login response
 *   ticket_id.json    — ticket ID created during test run
 *   comment_id.json   — comment ID for update/delete tests
 *   progression_id.json — progression ID for update/delete tests
 */
public class JsonFileManager {

    /**
     * Reads a string value from a JSON file by key.
     *
     * @param filePath path to the JSON file (e.g., "src/main/resources/json/token.json")
     * @param key      the JSON key to look up (e.g., "token")
     * @return the string value associated with the key
     * @throws RuntimeException if the file cannot be read or the key is missing
     */
    public static String readValue(String filePath, String key) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content).getString(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read '" + key + "' from " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Writes a single key-value pair to a JSON file, overwriting existing content.
     *
     * Example output: { "token": "eyJhbGci..." }
     *
     * @param filePath path to the JSON file to write
     * @param key      the JSON key (e.g., "ticketId")
     * @param value    the string value to store
     * @throws RuntimeException if the file cannot be written
     */
    public static void writeValue(String filePath, String key, String value) {
        try (FileWriter fw = new FileWriter(filePath)) {
            JSONObject json = new JSONObject();
            json.put(key, value);
            fw.write(json.toString(2));
        } catch (Exception e) {
            throw new RuntimeException("Failed to write '" + key + "' to " + filePath + ": " + e.getMessage());
        }
    }
}
