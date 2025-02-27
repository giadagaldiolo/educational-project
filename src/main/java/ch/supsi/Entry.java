package ch.supsi;

import java.util.List;

public record Entry(String seriesTitle, int releaseYear, int runtime, double imdbRating, String director, List<String> stars) {
}