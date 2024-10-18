package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Username is required")  // Ensures the username is not blank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")  // Ensures the password is not blank
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")  // Ensures valid email format
    @Column(unique = true, nullable = false)
    private String email;

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_genres", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "Genre")
    @Fetch(FetchMode.JOIN)  // You can change this to LAZY if performance becomes a concern
    private List<Genre> genres = new ArrayList<>();

    // Default constructor
    public User() {}

    // Constructor including genres
    public User(String username, String password, String email, List<Genre> genres) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.genres = genres != null ? genres : new ArrayList<>();
    }

    // Constructor without genres (common case for user creation)
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.genres = new ArrayList<>();
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
        this.password = password;  // Ensure this is hashed before saving in the service layer
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
