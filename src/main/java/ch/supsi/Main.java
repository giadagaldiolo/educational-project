package ch.supsi;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        // Get the input and output file paths from UserPreferences and convert them to absolute paths if they are not
        Path inputPath = Paths.get(UserPreferences.getInputFilePath());
        Path outputPath = Paths.get(UserPreferences.getOutputFilePath());

        // Read the movie entries from the input CSV file
        List<Entry> entries = FileHandler.readEntries(inputPath);

        // Calculate various statistics based on the movie entries
        int totalMovies = Statistics.totalMovies(entries); // Total number of movies
        double averageRunTime = Statistics.averageRunTime(entries); // Average runtime of movies
        String bestDirector = Statistics.bestDirector(entries); // Director with the best movies
        String mostPresentActor = Statistics.mostPresentActor(entries); // Actor appearing most often in the movies
        int mostProductiveYear = Statistics.mostProductiveYear(entries); // Year with the most movie releases

        // Write the calculated statistics to the output CSV file
        FileHandler.writeStatistics(outputPath, totalMovies, averageRunTime, bestDirector, mostPresentActor, mostProductiveYear);
    }
}