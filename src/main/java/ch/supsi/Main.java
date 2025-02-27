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


        //TODO: usa la classe Preferencies
        //TODO: scrivi se il file di output esiste già o viene creato ora

        // Percorso del file di preferenze
        Path preferencesPath = Paths.get("src", "main", "resources", "preferences.txt"); // Uso di Paths.get() per garantire compatibilità con Windows, Mac e Linux.

        // Legge le preferenze
        Map<String, String> prefs = FileHandler.readPreferences(preferencesPath);
        Path inputPath = Paths.get(prefs.getOrDefault("input_file", "resources/imdb_top_1000.csv"));
        Path outputPath = Paths.get(prefs.getOrDefault("output_file", "resources/output.csv"));
//        System.out.println("File di input: " + inputPath);
//        System.out.println("File di output: " + outputPath);

        System.out.println("preferences file absolute path: " + preferencesPath.toAbsolutePath());
        System.out.println("Input file absolute path: " + inputPath.toAbsolutePath());
        System.out.println("Output file absolute path: " + outputPath.toAbsolutePath());

        // Lettura file
        List<Entry> entries = FileHandler.readEntries(inputPath);

        //Calcola statistiche
        int totalMovies = Statistics.totalMovies(entries);
        double averageRunTime = Statistics.averageRunTime(entries);
        String bestDirector = Statistics.bestDirector(entries);
        String mostPresentActor = Statistics.mostPresentActor(entries);
        int mostProductiveYear = Statistics.mostProductiveYear(entries);

        // Scrittura risultati
        FileHandler.writeStatistics(outputPath, totalMovies, averageRunTime, bestDirector, mostPresentActor, mostProductiveYear);
    }
}