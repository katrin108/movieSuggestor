package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;

import java.util.List;

public interface WatchedService {
    Movie saveMovie(Movie movie);
    void deleteMovie(Movie movie);
    Movie findMovieById(long id);

    Watched saveWatched(Watched watched);
    void deleteWatched(Watched watched);
    List<Watched> findAllWatchedLists();
    Watched findWatchedById(long id);

    Watched addMovieToList(Watched watched, Movie movie);
    Watched removeMovieFromList(Watched watched, Movie movie);
}
