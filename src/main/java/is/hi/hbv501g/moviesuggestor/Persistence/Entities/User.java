package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    //baeta vid meira seinna
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private Boolean child;
    // can add more

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_genres", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "genre")
    @Fetch(FetchMode.JOIN)
    private List<Genre> genres = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<MovieList> movieLists;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Watched watched;

    public User() {

        child=false;
        movieLists=new ArrayList<>();
        watched=new Watched();
    }

    public User(String username, String password, String email, List<Genre> genres, Boolean child) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.child = child;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getChild() {
        return child;
    }

    public void setChild(Boolean child) {
        this.child = child;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<MovieList> getMovieLists() {
        return movieLists;
    }

    public void setMovieLists(List<MovieList> movieLists) {
        this.movieLists = movieLists;
    }
    public void setWatched(Watched watched) {this.watched = watched;}

    public Watched getWatched() {

        return watched;
    }



}
