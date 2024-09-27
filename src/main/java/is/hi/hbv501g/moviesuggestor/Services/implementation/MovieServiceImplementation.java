package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.MovieRepository;

import java.util.List;
@Service
public class MovieServiceImplementation implements MovieService {

    private MovieRepository movieRepository;

    @Autowired
    public MovieServiceImplementation(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;

    }




    @Override
    public Movie findMovieByTitle(String title) {

        return movieRepository.findByTitle(title).get(0);

    }

    @Override
    public List<Movie> findAllMovies() {

        return movieRepository.findAll();
    }

    @Override
    public Movie findMovieById(long id) {
        return movieRepository.findAllById(id);
    }

    @Override
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie);

    }
}
