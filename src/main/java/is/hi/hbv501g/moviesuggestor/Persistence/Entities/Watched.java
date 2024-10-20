package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name ="WatchedList")

public class Watched {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany
    private List<Movie> movies = new ArrayList<Movie>();

    public Watched() {
        movies = new ArrayList<Movie>();
    }

    public Watched(List<Movie> movies) { this.movies = movies; }

    public Watched(Movie movie) { this.movies.add(movie); }

    public long getID() { return id; }

    public void setID(long id) { this.id = id; }

    public List<Movie> getMovies() { return movies; }

    public void setMovies(List<Movie> movies) { this.movies = movies; }

    public void addMovie(Movie movie) { this.movies.add(movie); }

    public void addMovies(List<Movie> movies) { this.movies.addAll(movies); }

    /*public List<Movie> getMoviesByTitle(String title) {
        List<Movie> placeholder = new ArrayList<Movie>();
        return placeholder;
    }

    public Movie getMovieByID(long id) { return new Movie();}

     */
}
