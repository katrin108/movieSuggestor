package is.hi.hbv501g.moviesuggestor.Persistence.Entities;


import jakarta.persistence.*;

@Entity
@Table(name = "watchHistory")

public class movieList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //margir listar tilheyra einum notanda
    @ManyToOne(fetch = FetchType.LAZY)

    private User user;

    //Hver listi hefur margar myndir
    @ManyToOne(fetch = FetchType.LAZY)

    private Movie movie;

    public movieList() {
    }

    public movieList(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
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

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
