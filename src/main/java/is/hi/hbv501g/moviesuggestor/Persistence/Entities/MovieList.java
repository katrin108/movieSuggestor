package is.hi.hbv501g.moviesuggestor.Persistence.Entities;


import jakarta.persistence.*;

@Entity
@Table(name = "MovieList")

public class MovieList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    //Hver listi hefur margar myndir
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    public MovieList(String name) {
        this.name = name;
    }

    public MovieList(String name, Movie movie) {
        this.name = name;
        this.movie = movie;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
