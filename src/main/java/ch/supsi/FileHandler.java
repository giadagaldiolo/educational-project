package ch.supsi;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    // Method to read entries from a CSV file and return a list of Entry objects
    public static List<Entry> readEntries(Path inputPath) {
        List<Entry> entries = new ArrayList<>();

        // Create a CSVReader to read the rows from the input file
        try (CSVReader reader = new CSVReader(new FileReader(inputPath.toFile()))) {
            reader.readNext(); // Skip the header row
            String[] fields;

            // Loop through each row in the CSV file
            while ((fields = reader.readNext()) != null) {
                // Skip rows with invalid column length
                if (fields.length != 16) continue;

                // Extract the list of stars from columns 10 to 13
                List<String> stars = List.of(fields[10], fields[11], fields[12], fields[13]);

                // Create a new Entry object with parsed data and add it to the entries list
                entries.add(new Entry(fields[1], parseInt(fields[2]), parseRuntime(fields[4]), parseDouble(fields[6]), fields[9], stars));
            }
        } catch (IOException | CsvValidationException e) {
            // Handle any errors that occur during reading
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }

        // Return the list of entries read from the file
        return entries;
    }

    // Method to parse the runtime from a string (e.g., "120 min") and return an integer value
    private static int parseRuntime(String s) {
        try {
            return parseInt(s.replace(" min", "").trim()); // Remove " min" and parse the integer
        } catch (NumberFormatException e) {
            // If there's an error parsing the runtime, return -1 as a fallback value
            return -1;
        }
    }

    // Method to write statistics to a CSV file
    public static void writeStatistics(Path outputPath, int totalMovies, double averageRunTime, String bestDirector, String mostPresentActor, int mostProductiveYear) {
        try {
            // Ensure the output directory exists, create if necessary
            Files.createDirectories(outputPath.getParent());
            boolean fileExists = Files.exists(outputPath);

            // Create a CSVWriter to write the statistics to the output file
            try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath.toFile()))) {
                // Write the header row
                writer.writeNext(new String[]{"Statistic", "Value"});
                // Write each statistic as a new row
                writer.writeNext(new String[]{"Total Movies", String.valueOf(totalMovies)});
                writer.writeNext(new String[]{"Average Runtime", averageRunTime + " min"});
                writer.writeNext(new String[]{"Best Director", bestDirector});
                writer.writeNext(new String[]{"Most Present Actor", mostPresentActor});
                writer.writeNext(new String[]{"Most Productive Year", String.valueOf(mostProductiveYear)});
            }

            // Print a message indicating where the statistics have been written
            System.out.println(fileExists ? "Statistics overwritten onto " + outputPath.toAbsolutePath()
                    : "Statistics written to " + outputPath.toAbsolutePath());

        } catch (IOException e) {
            // Handle any errors that occur during writing
            System.err.println("Errore nella scrittura del file CSV: " + e.getMessage());
        }
    }


}
