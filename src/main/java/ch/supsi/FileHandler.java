package ch.supsi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        try {
            // Crea le cartelle se non esistono
            Files.createDirectories(outputPath.getParent()); // Unlike the createDirectory method, an exception is not thrown if the directory could not be created because it already exists.
            boolean fileExists = Files.exists(outputPath);
            try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
                writer.write("Statistic,Value\n");
                writer.write("Total Movies," + totalMovies + "\n");
                writer.write("Average Runtime," + averageRunTime + " min\n");
                writer.write("Best Director," + bestDirector + "\n");
                writer.write("Most Present Actor," + mostPresentActor + "\n");
                writer.write("Most Productive Year," + mostProductiveYear + "\n");
            }
            if (fileExists) {
                System.out.println("Statistics overwritten onto " + outputPath.toAbsolutePath());
            } else {
                System.out.println("Statistics written to " + outputPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file: " + e.getMessage());
        }
    }



}
