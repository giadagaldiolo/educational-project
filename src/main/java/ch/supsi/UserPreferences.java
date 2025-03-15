package ch.supsi;

import javax.json.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class UserPreferences {
    // Define the path where the preferences file will be stored
    private static final Path DIRECTORY_PATH = Paths.get(System.getProperty("user.home"), "educationalProjectData");
    public static final Path PREFERENCES_FILE = DIRECTORY_PATH.resolve("preferences.json");
    private static Map<String, String> preferences = new HashMap<>();

    // Static initialization block that calls the loadPreferences() method
    // when the class is first loaded. This loads the preferences from the preferences.json file
    // or sets default preferences if the file doesn't exist or an error occurs while reading.
    static {
        loadPreferences();
    }

    // Method to load preferences from the preferences.json file
    private static void loadPreferences() {
        // Check if the preferences file exists
        if (Files.exists(PREFERENCES_FILE)) {
            try (InputStream is = Files.newInputStream(PREFERENCES_FILE);
                 JsonReader reader = Json.createReader(is)) {
                // Parse the JSON data from the file and store it in the preferences map
                preferences = parseJson(reader.readObject());
            } catch (IOException e) {
                // If an error occurs while reading the file, print an error message and create default preferences
                System.err.println("Error reading preferences: " + e.getMessage());
                createDefaultPreferences();
            }
        } else {
            // If the file doesn't exist, create default preferences
            createDefaultPreferences();
        }
    }

    // Method to create default preferences and save them to the file
    private static void createDefaultPreferences() {
        // Set empty values for preferences
        preferences.put("input_file", "");
        preferences.put("output_file", "");

        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(DIRECTORY_PATH);  // Crea la cartella se non esiste
            // Save the default preferences to the preferences.json file
            savePreferences();
            System.out.println("File preferences.json created at " + PREFERENCES_FILE);
        } catch (IOException e) {
            // If an error occurs during directory or file creation, print an error message
            System.err.println("Error creating preferences: " + e.getMessage());
        }
    }

    // Method to save the preferences to the preferences.json file
    private static void savePreferences() {
        try (OutputStream os = Files.newOutputStream(PREFERENCES_FILE);
             JsonWriter writer = Json.createWriter(os)) {
            // Convert the preferences map to JSON and write it to the file
            writer.writeObject(toJson(preferences));
        } catch (IOException e) {
            // If an error occurs while writing to the file, print an error message
            System.err.println("Error writing preferences: " + e.getMessage());
        }
    }

    // Method to get the input file path, using the value from preferences or a default if empty
    public static String getInputFilePath() {
        // Default input file path
        Path inputPath = DIRECTORY_PATH.resolve("imdb_top_1000.csv");
        // Retrieve the input file path from preferences, or use the default if not set
        String filePath = preferences.getOrDefault("input_file", inputPath.toString());

        // Check if the retrieved file path is empty and use the fallback if necessary
        if (filePath.isEmpty()) {
            filePath = inputPath.toString(); // Use the fallback
        }

        return filePath;
    }

    // Method to get the output file path, using the value from preferences or a default if empty
    public static String getOutputFilePath() {
        // Default output file path
        Path outputPath = DIRECTORY_PATH.resolve("output.csv");
        // Retrieve the output file path from preferences, or use the default if not set
        String filePath = preferences.getOrDefault("output_file", outputPath.toString());

        // Check if the retrieved file path is empty and use the fallback if necessary
        if (filePath.isEmpty()) {
            filePath = outputPath.toString(); // Use the fallback
        }

        return filePath;
    }

    // Method to convert a JSON object to a Map<String, String>
    private static Map<String, String> parseJson(JsonObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        // Iterate through the keys in the JSON object and add them to the map
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.getString(key, ""));
        }
        return map;
    }

    // Method to convert a Map<String, String> to a JSON object
    private static JsonObject toJson(Map<String, String> map) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        // Iterate through the map and add each key-value pair to the JSON object
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
