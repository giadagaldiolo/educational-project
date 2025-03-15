package ch.supsi;

import java.util.List;

public record Entry(String seriesTitle, int releaseYear, int runtime, double imdbRating, String director, List<String> stars) {
    // This is a record class (the fields are final)

    // 'seriesTitle' is the title of the movie or series
    // 'releaseYear' is the year when the movie or series was released
    // 'runtime' is the runtime of the movie/series in minutes
    // 'imdbRating' is the IMDb rating of the movie/series
    // 'director' is the name of the director of the movie/series
    // 'stars' is a list of actors/actresses (stars) who appeared in the movie/series
}