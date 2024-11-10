package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.MovieListRepository;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.MovieRepository;
import is.hi.hbv501g.moviesuggestor.Services.MovieListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieListServiceImplementation implements MovieListService {

    @Autowired
    private MovieListRepository movieListRepository;

    @Autowired
    private MovieRepository movieRepository;


    @Override
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie);
    }

    @Override
    public List<MovieList> findAll() {
        return movieListRepository.findAll();
    }

    @Override
    public Movie findMoviebyId(long id) {
        return movieRepository.findAllById(id);
    }

    @Override
    public void addMovieToList(MovieList movieList, Movie movie) {
        movieList.getMovies().add(movie);
        movieListRepository.save(movieList);
        System.out.println("The movies in the movie list"+movieList.getMovies());
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
    public MovieList findMovieListById(long id) {
        if(movieListRepository.existsById(id)){
            System.out.println("Movielist exists");
            return movieListRepository.findMovieListById(id);
        }
        else {
            System.out.println("Movielist does not exist");
            return null;
        }

    }

    @Override
    public MovieList findMovieListByName(String name){
        return movieListRepository.findMoveListByName(name);
    }


}
