package utils;

import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFileManager {

    public static String readValue(String filePath, String key) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content).getString(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read '" + key + "' from " + filePath + ": " + e.getMessage());
        }
    }

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
