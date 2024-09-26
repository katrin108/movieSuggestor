package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;

import java.util.List;

public interface MovieService {
    Movie findMovieByTitle(String title);
    List<Movie> findAllMovies();
    Movie findMovieById(long id);
    Movie saveMovie(Movie movie);
    void deleteMovie(Movie movie);
}
