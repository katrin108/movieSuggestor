package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.WatchedRepository;
import is.hi.hbv501g.moviesuggestor.Services.WatchedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WatchedListServiceImplementation implements WatchedService {
    private final WatchedRepository watchedRepository;


    @Autowired
    public WatchedListServiceImplementation(WatchedRepository watchedRepository) {
        this.watchedRepository = watchedRepository;
    }
    @Override
    public Watched getWatchedByUserId(Long id){
        return watchedRepository.findByUserId(id);
    }


    @Override
    public void save(Watched watched) {
        watchedRepository.save(watched);
    }


}
