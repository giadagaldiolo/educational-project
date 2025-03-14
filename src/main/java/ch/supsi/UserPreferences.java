package ch.supsi;

import javax.json.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class UserPreferences {
    private static final Path DIRECTORY_PATH = Paths.get(System.getProperty("user.home"), "educationalProjectData");
    public static final Path PREFERENCES_FILE = DIRECTORY_PATH.resolve("preferences.json");
    private static Map<String, String> preferences = new HashMap<>();

    // static initialization block
    /* calls the loadPreferences() method, which is responsible for loading the preferences
    from the file (preferences.json) when the class is first used. This means that as soon as
    the class is loaded, the preferences map will be initialized with the data from the preferences
    file (if it exists), or it will set default preferences if the file doesn't exist or if an error
    occurs while reading it.
     */
    static {
        loadPreferences();
    }

    private static void loadPreferences() {
        if (Files.exists(PREFERENCES_FILE)) {
            try (InputStream is = Files.newInputStream(PREFERENCES_FILE);
                 JsonReader reader = Json.createReader(is)) {
                preferences = parseJson(reader.readObject());
            } catch (IOException e) {
                System.err.println("Errore nella lettura delle preferenze: " + e.getMessage());
                createDefaultPreferences();
            }
        } else {
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
        return preferences.getOrDefault("input_file", inputPath.toString());
    }

    public static String getOutputFilePath() {
        Path outputPath = DIRECTORY_PATH.resolve("output.csv");
        return preferences.getOrDefault("output_file", outputPath.toString());
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
