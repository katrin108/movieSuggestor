package is.hi.hbv501g.moviesuggestor.Services;


import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User login(User user);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserById(long id);

    void saveUser(User user);
    void deleteUser(User user);
    void setGenres(User user,List<Genre> genres);

    MovieList findMovieListById(long id);
    MovieList findMoveListByTitle(String title);

    MovieList saveMovieList(MovieList movieList);
    void deleteMovieList(MovieList movieList);

}
