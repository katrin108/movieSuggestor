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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",unique = true)
    private User user;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "watched_movies",
            joinColumns = @JoinColumn(name = "watched_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private List<Movie> movies;

    public Watched() {
        this.movies = new ArrayList<>();
    }
    public Watched(User user,List<Movie> movies) {
        this.user = user;
        this.movies = movies !=null?movies:new ArrayList<>();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void addMovie(Movie movie) {
        this.movies.add(movie);
    }
    public void removeMovie(Movie movie) {
        if (this.movies != null) {
            System.out.println("delete movie");
            this.movies.remove(movie);
        }
    }
}
