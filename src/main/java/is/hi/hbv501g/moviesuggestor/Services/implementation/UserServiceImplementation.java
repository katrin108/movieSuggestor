package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.UserRepository;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository; // Use UserRepository for persistence

    @Autowired
    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository; // Inject UserRepository via constructor
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll(); // Return all users
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username); // Find a user by username
    }

    @Override
    public User findUserByEmail(String email) {
        // Implement this method if needed
        return null;
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findById(id).orElse(null); // Find a user by ID
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user); // Save or update the user
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user); // Delete the user
    }

    @Override
    public User login(User user) {
        User existingUser = findUserByUsername(user.getUsername());
        // Check if the user exists and if the password matches
        if (existingUser != null && Objects.equals(existingUser.getPassword(), user.getPassword())) {
            return existingUser; // Return the logged-in user
        }
        return null; // Return null if login fails
    }

    @Override
    public void setGenres(User user, List<Genre> genres) {
        user.setGenres(genres);  // Set the user's preferred genres
        userRepository.save(user); // Persist the user's updated preferences
    }
}
