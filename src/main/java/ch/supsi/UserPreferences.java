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
                System.err.println("Errore nella lettura delle preferenze: " + e.getMessage());
                createDefaultPreferences();
            }
        } else {
            // If the file doesn't exist, create default preferences
            createDefaultPreferences();
        }
    }

    private static void createDefaultPreferences() {
        preferences.put("input_file", "");
        preferences.put("output_file", "");

        try {
            Files.createDirectories(DIRECTORY_PATH);  // Crea la cartella se non esiste
            savePreferences();
            System.out.println("File preferences.json creato in " + PREFERENCES_FILE);
        } catch (IOException e) {
            System.err.println("Errore nella creazione delle preferenze: " + e.getMessage());
        }
    }

    private static void savePreferences() {
        try (OutputStream os = Files.newOutputStream(PREFERENCES_FILE);
             JsonWriter writer = Json.createWriter(os)) {
            writer.writeObject(toJson(preferences));
        } catch (IOException e) {
            System.err.println("Errore nella scrittura delle preferenze: " + e.getMessage());
        }
    }

    public static String getInputFilePath() {
        Path inputPath = DIRECTORY_PATH.resolve("imdb_top_1000.csv");
        String filePath = preferences.getOrDefault("input_file", inputPath.toString());

        // Verifica se la stringa è vuota e usa il valore di fallback
        if (filePath.isEmpty()) {
            filePath = inputPath.toString(); // Usa il fallback
        }

        return filePath;
    }

    public static String getOutputFilePath() {
        Path outputPath = DIRECTORY_PATH.resolve("output.csv");
        String filePath = preferences.getOrDefault("output_file", outputPath.toString());

        // Verifica se la stringa è vuota e usa il valore di fallback
        if (filePath.isEmpty()) {
            filePath = outputPath.toString(); // Usa il fallback
        }

        return filePath;
    }

    // Metodo per convertire una stringa JSON in una Map<String, String>
    private static Map<String, String> parseJson(JsonObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.getString(key, ""));
        }
        return map;
    }


    private static JsonObject toJson(Map<String, String> map) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
