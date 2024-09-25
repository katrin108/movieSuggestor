package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import java.util.List;

public class Movie {
    private long id;
    private String title;
    private List<Genre> genre;
    private String description;
    private String releaseDate;
    private double runtime;
    private double rating;

    public Movie() {
    }

    public Movie(String title, List<Genre> genre, String description, String releaseDate, double runtime, double rating) {
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.rating = rating;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Genre> getGenre() {
        return genre;
    }

    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRuntime() {
        return runtime;
    }

    public void setRuntime(double runtime) {
        this.runtime = runtime;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
