package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.MovieRepository;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.WatchedRepository;
import is.hi.hbv501g.moviesuggestor.Services.WatchedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchedServiceImplementation implements WatchedService {

    @Autowired
    private WatchedRepository watchedRepository;

    private MovieRepository movieRepository;

    @Autowired
    public WatchedServiceImplementation(WatchedRepository watchedRepository) {
        this.watchedRepository = watchedRepository;
    }

    @Override
    public Movie addMovie(Movie movie) { return movieRepository.save(movie); }
    @Override
    public void deleteMovie(Movie movie) { movieRepository.delete(movie); }






    @Override
    public List<Watched> findAll() {
        return watchedRepository.findAll();
    }
    @Override
    public Watched findWatchedById(long id) {
        return watchedRepository.findById(id);
    }

    @Override
    public Watched addMovieToList(Watched watched, Movie movie) {
        watched.getMovies().add(movie);
        return watchedRepository.save(watched);
    }

    @Override
    public Watched saveWatched(Watched watched) {
        return watchedRepository.save(watched);
    }

    @Override
    public void deleteWatched(Watched watched) {

        watchedRepository.delete(watched);
    }








}
