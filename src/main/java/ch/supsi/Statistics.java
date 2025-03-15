package ch.supsi;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistics {

    // Calculates the total number of movies
    public static int totalMovies(List<Entry> entries) {
        return entries.size(); // Simply return the size of the entries list
    }

    // Calculates the average movies run-time
    public static double averageRunTime(List<Entry> entries) {
        return entries.stream()
                .map(Entry::runtime)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }


    // Calculates the best director based on the highest average IMDb rating
    public static String bestDirector(List<Entry> entries) {
        // Create lists of directors and IMDb ratings
        List<String> directors = entries.stream().map(Entry::director).toList();
        List<Double> imdbRatings = entries.stream().map(Entry::imdbRating).toList();

        // Find the director with the highest average IMDb rating
        return entries.stream()
                .map(Entry::director)
                .distinct()
                .max(Comparator.comparingDouble(director -> getDirectorAverageRating(director, directors, imdbRatings)))
                .orElse("Null");
    }


    // Finds the most present actor/actress across all movies
    public static String mostPresentActor(List<Entry> entries) {
        return entries.stream()
                .flatMap(entry -> entry.stars().stream())
                .collect(Collectors.groupingBy(star -> star, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Null");
    }

    // Calculates the most productive year
    public static int mostProductiveYear(List<Entry> entries) {
        return entries.stream()
                .map(Entry::releaseYear)
                .collect(Collectors.groupingBy(year -> year, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    // Helper method to calculate the average IMDb rating for a director
    private static double getDirectorAverageRating(String director, List<String> directors, List<Double> ratings) {
        double sum = 0;
        int count = 0;

        // Loop through the directors and ratings to calculate the sum and count
        for (int i = 0; i < directors.size(); i++) {
            if (directors.get(i).equals(director) && ratings.get(i) > 0) {
                sum += ratings.get(i); // Add the rating if it is greater than 0
                count++;
            }
        }

        // Return the average rating or 0 if no valid ratings were found
        return count > 0 ? sum / count : 0;
    }
}
