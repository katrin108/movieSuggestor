package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.WatchedRepository;
import is.hi.hbv501g.moviesuggestor.Services.WatchedService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class WatchedServiceImplementation implements WatchedService {

    private WatchedRepository watchedRepository;

    @Autowired
    public WatchedServiceImplementation(WatchedRepository watchedRepository) {
        this.watchedRepository = watchedRepository;
    }

    @Override
    public Watched saveWatched(Watched watched) {
        return this.watchedRepository.save(watched);
    }

    @Override
    public void deleteWatched(Watched watched) {
        watchedRepository.delete(watched);
    }

    @Override
    public List<Watched> findAllWatchedLists() {
        return watchedRepository.findAll();
    }

    @Override
    public Watched findWatchedById(long id) {
        return watchedRepository.findById(id);
    }
/*
    @Override
    public void deleteMovieById(long id) {
        watchedRepository.delete(watchedRepository.find(id));
    }
*/
}
