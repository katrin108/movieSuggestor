package is.hi.hbv501g.moviesuggestor.Services;


import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User login(User user);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserById(long id);

    User saveUser(User user);
    void deleteUser(User user);
}
