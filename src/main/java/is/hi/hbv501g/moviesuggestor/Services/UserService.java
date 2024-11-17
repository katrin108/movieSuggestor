package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> findAllUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserById(long id);
    void saveUser(User user);
    void deleteUser(User user);
    User login(User user);
    void setGenres(User user, List<Genre> genres);
    MovieList findMovieListById(long id);
    MovieList findMoveListByTitle(String name);
    MovieList saveMovieList(MovieList movieList);
    Watched saveWatched(Watched watched);
    void deleteMovieList(MovieList movieList);

}
