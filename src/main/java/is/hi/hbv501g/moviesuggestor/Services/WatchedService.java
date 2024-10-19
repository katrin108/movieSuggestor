package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;

import java.util.List;

public interface WatchedService {
    List<Watched> findAllWatched();

    Watched saveWatched(Watched watched);
    void deleteMovieById(long id);
}
