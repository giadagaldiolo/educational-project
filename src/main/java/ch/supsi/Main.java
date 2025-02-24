package ch.supsi;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        // Percorso del file di preferenze
        Path preferencesPath = Paths.get("src", "main", "resources", "preferences.txt"); // Uso di Paths.get() per garantire compatibilità con Windows, Mac e Linux.

        // Legge le preferenze
        Map<String, String> prefs = readPreferences(preferencesPath);
        Path inputPath = Paths.get(prefs.getOrDefault("input_file", "imdb_top_1000.csv"));
        Path outputPath = Paths.get(prefs.getOrDefault("output_file", "output.csv"));

        System.out.println("File di input: " + inputPath);
        System.out.println("File di output: " + outputPath);

        // ok
        // TODO: possiamo ignorare i campi che non ci servono?
        List<String> seriesTitles = new ArrayList<>();
        List<Integer> releaseYears = new ArrayList<>();
        List<Integer> runtimes = new ArrayList<>();
        List<Double> imdbRatings = new ArrayList<>();
        List<String> directors = new ArrayList<>();
        List<String> stars = new ArrayList<>();

        String line;

        try (BufferedReader br = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) { // Specifica UTF-8 per evitare problemi con caratteri speciali tra OS diversi.
            br.readLine(); // Salta l'intestazione

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // -1 per mantenere i campi vuoti

                if (fields.length < 16) continue; // Verifica che ci siano abbastanza colonne

                seriesTitles.add(fields[1]);
                releaseYears.add(parseInt(fields[2]));
                runtimes.add(parseRuntime(fields[4]));
                imdbRatings.add(parseDouble(fields[6]));
                directors.add(fields[9]);
                stars.add(fields[10]);
                stars.add(fields[11]);
                stars.add(fields[12]);
                stars.add(fields[13]);
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
            e.printStackTrace();
        }

        // total number of movies
        int totalMovies = seriesTitles.size();

        // average movies run-time
        double averageRunTime = runtimes.stream()
                        .mapToInt(Integer::intValue).average().orElse(0);


        // best director (media IMDb più alta)
        String bestDirector = directors.stream()
                .distinct()
                .max(Comparator.comparingDouble(director -> getDirectorAverageRating(director, directors, imdbRatings))).orElse("Null");

        // most present actor/actress
        String mostPresentActor = stars.stream()
                .collect(Collectors.groupingBy(star -> star, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Null");

        // most productive year
        int mostProductiveYear = releaseYears.stream()
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
            e.printStackTrace();
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
        try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
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

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1; // Valore di default
        }
    }

    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static int parseRuntime(String s) {
        try {
            return Integer.parseInt(s.replace(" min", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}