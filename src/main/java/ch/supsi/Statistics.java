package ch.supsi;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistics {

    // total number of movies
    public static int totalMovies(List<Entry> entries) {
        return entries.size();
    }

    // average movies run-time
    public static double averageRunTime(List<Entry> entries) {
        return entries.stream()
                .map(Entry::runtime)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }


    // best director (media IMDb più alta)
    public static String bestDirector(List<Entry> entries) {
        List<String> directors = entries.stream().map(Entry::director).toList();
        List<Double> imdbRatings = entries.stream().map(Entry::imdbRating).toList();

        return entries.stream()
                .map(Entry::director)
                .distinct()
                .max(Comparator.comparingDouble(director -> getDirectorAverageRating(director, directors, imdbRatings))).orElse("Null");
    }


    // most present actor/actress
    public static String mostPresentActor(List<Entry> entries) {
        return entries.stream()
                .flatMap(entry -> entry.stars().stream())
                .collect(Collectors.groupingBy(star -> star, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Null");
    }

    // most productive year
    public static int mostProductiveYear(List<Entry> entries) {
        return entries.stream()
                .map(Entry::releaseYear)
                .collect(Collectors.groupingBy(year -> year, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
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
}
