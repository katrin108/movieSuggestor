package is.hi.hbv501g.moviesuggestor.Services;


import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import java.util.List;

public interface UserService {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserById(long id);
    List<User> findAllUsers();
    User saveUser(User user);
    void deleteUser(User user);
    User login(User user );
    void setGenres(User user, List<Genre> genres);
}
