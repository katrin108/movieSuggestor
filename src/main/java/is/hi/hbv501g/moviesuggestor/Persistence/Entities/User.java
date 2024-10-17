package is.hi.hbv501g.moviesuggestor.Persistence.Entities;


import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;
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
    @Column(unique = true, nullable = false)
    private String email;
    //can add more

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_genres", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "Genre")
    @Fetch(FetchMode.JOIN)
    private List<Genre> genres=new ArrayList<>();



/* Það þarf að laga þetta !
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    Favorites userFavorites;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    Watched userWatched;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    ToWatch userToWatch;*/
    
    public User() {}


    public User(String username, String password, String email, List<Genre> genres) {

        this.username = username;
        this.password = password;
        this.email = email;
        this.genres =genres != null ? genres : new ArrayList<>();
    }



    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }



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
}
