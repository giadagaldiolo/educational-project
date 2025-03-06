package ch.supsi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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
        try {
            if (Files.exists(PREFERENCES_FILE)) {
                // Legge il JSON come stringa
                String content = Files.readString(PREFERENCES_FILE);
                preferences = parseJson(content);
            } else {
                createDefaultPreferences();
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura delle preferenze: " + e.getMessage());
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
        try (BufferedWriter writer = Files.newBufferedWriter(PREFERENCES_FILE)) {
            writer.write(toJson(preferences)); // Scrive il JSON nel file
        } catch (IOException e) {
            System.err.println("Errore nella scrittura delle preferenze: " + e.getMessage());
        }
    }

    public static String getInputFilePath() {
        Path inputPath = DIRECTORY_PATH.resolve("imdb_top_1000.csv");
        System.out.println("prova: " + inputPath);
        return preferences.getOrDefault("input_file", inputPath.toString());
    }

    public static String getOutputFilePath() {
        Path outputPath = DIRECTORY_PATH.resolve("output.csv");
        return preferences.getOrDefault("output_file", outputPath.toString());
    }

    // Metodo per convertire una stringa JSON in una Map<String, String>
    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().replaceAll("[{}\"]", ""); // Rimuove { } e "
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }

    // Metodo per convertire una Map<String, String> in formato JSON
    private static String toJson(Map<String, String> map) {
        StringBuilder json = new StringBuilder("{\n");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json.append("    \"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\",\n");
        }
        if (!map.isEmpty()) json.setLength(json.length() - 2); // Rimuove l'ultima virgola
        json.append("\n}");
        return json.toString();
    }
}
