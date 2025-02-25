package ch.supsi;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class Main {
    public static void main(String[] args) {

        // Percorso del file di preferenze
        Path preferencesPath = Paths.get("src", "main", "resources", "preferences.txt"); // Uso di Paths.get() per garantire compatibilità con Windows, Mac e Linux.

        // Legge le preferenze
        Map<String, String> prefs = readPreferences(preferencesPath);
        Path inputPath = Paths.get(prefs.getOrDefault("input_file", "resources/imdb_top_1000.csv"));
        Path outputPath = Paths.get(prefs.getOrDefault("output_file", "resources/output.csv"));
//        System.out.println("File di input: " + inputPath);
//        System.out.println("File di output: " + outputPath);

        System.out.println("preferences file absolute path: " + preferencesPath.toAbsolutePath());
        System.out.println("Input file absolute path: " + inputPath.toAbsolutePath());
        System.out.println("Output file absolute path: " + outputPath.toAbsolutePath());

        List<Entry> entries = new ArrayList<>();
        String line;

        try (BufferedReader br = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) { // Specifica UTF-8 per evitare problemi con caratteri speciali tra OS diversi.
            br.readLine(); // Salta l'intestazione

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // -1 per mantenere i campi vuoti

                if (fields.length < 16) continue; // Verifica che ci siano abbastanza colonne

                List<String> stars = new ArrayList<>();
                stars.add(fields[10]);
                stars.add(fields[11]);
                stars.add(fields[12]);
                stars.add(fields[13]);

                entries.add(new Entry(fields[1],parseInt(fields[2]),parseRuntime(fields[4]),parseDouble(fields[6]),fields[9], stars));

            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
            e.printStackTrace();
        }

        // total number of movies
        int totalMovies = entries.size();

        // average movies run-time
        double averageRunTime = entries.stream()
                .map(Entry::getRuntime)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);


        // best director (media IMDb più alta)
        List<String> directors = entries.stream()
                .map(Entry::getDirector).toList();
        List<Double> imdbRatings = entries.stream()
                .map(Entry::getImdbRating).toList();

        String bestDirector = entries.stream()
                .map(Entry::getDirector)
                .distinct()
                .max(Comparator.comparingDouble(director -> getDirectorAverageRating(director, directors, imdbRatings))).orElse("Null");


        // most present actor/actress
        String mostPresentActor = entries.stream()
                .flatMap(entry -> entry.getStars().stream())
                .collect(Collectors.groupingBy(star -> star, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Null");

        // most productive year
        int mostProductiveYear = entries.stream()
                .map(Entry::getReleaseYear)
                .collect(Collectors.groupingBy(year -> year, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);

        //Scrittura risultati
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

    private static double getDirectorAverageRating(String director, List<String> directors, List<Double> ratings) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < directors.size(); i++) {
            if (directors.get(i).equals(director) && ratings.get(i) > 0) {
                sum += ratings.get(i);
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
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

    private static int parseRuntime(String s) {
        try {
            return parseInt(s.replace(" min", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}