package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.MovieListRepository;
import is.hi.hbv501g.moviesuggestor.Services.MovieListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieListServiceImplementation implements MovieListService {

    private MovieListRepository movieListRepository;

    @Autowired
    public MovieListServiceImplementation(MovieListRepository movieListRepository) {
        this.movieListRepository = movieListRepository;
    }

    @Override
    public MovieList saveMovieList(MovieList movieList) {
        return movieListRepository.save(movieList);
    }

    @Override
    public void deleteMovieList(MovieList movieList) {
        movieListRepository.delete(movieList);
    }


    @Override
    public List<MovieList> findAllMovieLists() {
        return movieListRepository.findAll();
    }

    /*
    @Override
    public MovieList findMovieListById(long id) {
        return movieListRepository.findById(id);
    }
*/





}
