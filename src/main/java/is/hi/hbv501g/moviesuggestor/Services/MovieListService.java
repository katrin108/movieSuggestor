package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;

public interface MovieListService {
    MovieList save(MovieList movieList);
    void deleteMovieList(MovieList movieList);

    MovieList findAllMovieLists();
    MovieList findMoveListByTitle(String title);
    MovieList findMovieListById(long id);

    Movie saveMovie(Movie movie);
    void deleteMovie(Movie movie);
    Movie findAllMovies();
    Movie findMoviebyId(long id);
}
