package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.WatchedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WatchedService {
    Movie addMovie(Movie movie);
    void deleteMovie(Movie movie);
    List<Watched> findAll();
    Watched findWatchedById(long id);
    Watched addMovieToList(Watched watched, Movie movie);
    Watched saveWatched(Watched watched);
    public void deleteWatched(Watched watched);




}
