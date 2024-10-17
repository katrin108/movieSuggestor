package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="FavoritesList")

public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private User user;

    @OneToMany
    private List<Movie> movies;

    public ToWatch() {

    }

    public ToWatch(User user) {
        this.user = user;

    }

    public getID() { return id; }

    public void setID(long id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public List<Movie> getMovies() { return movies; }

    public List<Movie> getMoviesByTitle(String title) {}

    public Movie getMovieByID(long id) {}
}
