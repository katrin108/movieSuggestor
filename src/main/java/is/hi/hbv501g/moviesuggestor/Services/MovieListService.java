package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;

import java.util.List;

public interface MovieListService {

    Movie saveMovie(Movie movie);
    void deleteMovie(Movie movie);
    List<MovieList> findAll();
    Movie findMoviebyId(long id);

    void addMovieToList(MovieList movieList, Movie movie);

    MovieList saveMovieList(MovieList movieList);
    void deleteMovieList(MovieList movieList);

    MovieList findMovieListById(long id);

    MovieList findMovieListByName(String name);
}
