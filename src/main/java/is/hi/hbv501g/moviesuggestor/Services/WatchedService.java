package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;

import java.util.List;

public interface WatchedService {
    Watched saveWatched(Watched watched);
    void deleteWatched(Watched watched);

    List<Watched> findAllWatchedLists();
    Watched findWatchedById(long id);
}
