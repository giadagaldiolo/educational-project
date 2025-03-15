package ch.supsi;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class FileHandler {



    public static List<Entry> readEntries(Path inputPath) {
        List<Entry> entries = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(inputPath.toFile()))) {
            reader.readNext(); // Salta l'intestazione
            String[] fields;

            while ((fields = reader.readNext()) != null) {
                if (fields.length != 16) continue;

                List<String> stars = List.of(fields[10], fields[11], fields[12], fields[13]);
                entries.add(new Entry(fields[1], parseInt(fields[2]), parseRuntime(fields[4]), parseDouble(fields[6]), fields[9], stars));
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Errore nella lettura del file CSV: " + e.getMessage());
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
            Files.createDirectories(outputPath.getParent());
            boolean fileExists = Files.exists(outputPath);

            try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath.toFile()))) {
                writer.writeNext(new String[]{"Statistic", "Value"}); // Header
                writer.writeNext(new String[]{"Total Movies", String.valueOf(totalMovies)});
                writer.writeNext(new String[]{"Average Runtime", averageRunTime + " min"});
                writer.writeNext(new String[]{"Best Director", bestDirector});
                writer.writeNext(new String[]{"Most Present Actor", mostPresentActor});
                writer.writeNext(new String[]{"Most Productive Year", String.valueOf(mostProductiveYear)});
            }

            System.out.println(fileExists ? "Statistics overwritten onto " + outputPath.toAbsolutePath()
                    : "Statistics written to " + outputPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file CSV: " + e.getMessage());
        }
    }


}
