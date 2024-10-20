package is.hi.hbv501g.moviesuggestor.Persistence.Entities;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MovieList")

public class MovieList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //Hver listi hefur margar myndir
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "movie_list_movies",joinColumns = @JoinColumn(name = "movie_list_id"),inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private List<Movie> movies;

    public MovieList() {

    }

    public MovieList(String name, List<Movie> movies) {
        this.name = name;
        this.movies = movies;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
