package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="FavoritesList")

public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany
    private List<Movie> movies;



    public Favorites(){};

    public Favorites(User user) {
        //this.user = user;

    }

    public long getID() { return id; }

    public void setID(long id) { this.id = id; }

    //public User getUser() { return user; }

    //public void setUser(User user) { this.user = user; }

    public List<Movie> getMovies() { return movies; }

    public List<Movie> getMoviesByTitle(String title) {
        List<Movie> placeholder = new ArrayList<Movie>();
        return placeholder;
    }

    public Movie getMovieByID(long id) { return new Movie();}

    public List<Movie> findAllFavorites(List<Movie> movies) { return movies; }



}
