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
    private List<Movie> movies;

    public Watched(){}

    public long getID() { return id; }

    public void setID(long id) { this.id = id; }

    public List<Movie> getMovies() { return movies; }

    public List<Movie> getMoviesByTitle(String title) {
        List<Movie> placeholder = new ArrayList<Movie>();
        return placeholder;
    }

    public Movie getMovieByID(long id) { return new Movie();}
}
