package ch.supsi;

import java.util.ArrayList;
import java.util.List;

public class Entry {
    private String seriesTitle;
    private int releaseYear;
    private int runtime;
    private double imdbRating;
    private String director;
    private List<String> stars = new ArrayList<>();


    public Entry(String seriesTitle, int releaseYear, int runtime, double imdbRating, String director, List<String> stars) {
        this.seriesTitle = seriesTitle;
        this.releaseYear = releaseYear;
        this.runtime = runtime;
        this.imdbRating = imdbRating;
        this.director = director;
        this.stars = stars;
    }


    public String getSeriesTitle() {
        return seriesTitle;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getRuntime() {
        return runtime;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public String getDirector() {
        return director;
    }

    public List<String> getStars() {
        return stars;
    }
}
