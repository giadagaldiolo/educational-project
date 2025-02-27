package ch.supsi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class FileHandler {

    public static List<Entry> readEntries(Path inputPath) {
        List<Entry> entries = new ArrayList<>();
        String line;

        try (BufferedReader br = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) { // Specifica UTF-8 per evitare problemi con caratteri speciali tra OS diversi.
            br.readLine(); // Salta l'intestazione

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // -1 per mantenere i campi vuoti

                if (fields.length != 16) continue; // Verifica che ci siano abbastanza colonne

                List<String> stars = List.of(fields[10], fields[11], fields[12], fields[13]);
                entries.add(new Entry(fields[1], parseInt(fields[2]), parseRuntime(fields[4]), parseDouble(fields[6]), fields[9], stars));
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }

        return entries;
    }


    private static int parseRuntime(String s) {
        try {
            return parseInt(s.replace(" min", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    //scrittura risultati
    public static void writeStatistics(Path outputPath, int totalMovies, double averageRunTime, String bestDirector, String mostPresentActor, int mostProductiveYear) {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("Statistic,Value\n");
            writer.write("Total Movies," + totalMovies + "\n");
            writer.write("Average Runtime," + averageRunTime + " min\n");
            writer.write("Best Director," + bestDirector + "\n");
            writer.write("Most Present Actor," + mostPresentActor + "\n");
            writer.write("Most Productive Year," + mostProductiveYear + "\n");
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file: " + e.getMessage());
        }
    }

    public static Map<String, String> readPreferences(Path filePath) {
        Map<String, String> preferences = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filePath)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    preferences.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura delle preferenze: " + e.getMessage());
        }
        return preferences;
    }

}
