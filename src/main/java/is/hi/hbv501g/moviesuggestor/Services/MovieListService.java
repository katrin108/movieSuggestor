package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;

import java.util.List;

public interface MovieListService {
    MovieList saveMovieList(MovieList movieList);
    void deleteMovieList(MovieList movieList);

    List<MovieList> findAllMovieLists();
    //MovieList findMovieListById(long id);
    //MovieList findMoveListByTitle(String title);

    /*
    Movie saveMovie(Movie movie);
    void deleteMovie(Movie movie);
    Movie findAllMovies();
    Movie findMoviebyId(long id);
     */
}
