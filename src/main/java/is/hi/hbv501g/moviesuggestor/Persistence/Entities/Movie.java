package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name = "movies")

public class Movie {
    //baeta vid meira seinna
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;        //primary key

    private String title;

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "movie_genres",joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genra")
    private List<Genre> genre;
    private String description;
    private String releaseDate;
    private double runtime;
    private double rating;


    @OneToMany(mappedBy = "movie",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MovieList> MovieLists =new ArrayList<>();

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
