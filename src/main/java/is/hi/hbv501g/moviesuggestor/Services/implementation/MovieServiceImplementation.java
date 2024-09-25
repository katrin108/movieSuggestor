package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class MovieServiceImplementation implements MovieService {
    private List<Movie> moviesReposotory=new ArrayList<>();//just test
    private int id_counter=0;

    @Autowired
    public MovieServiceImplementation() {
        //test
        moviesReposotory.add(new Movie("T1", List.of(Genre.ACTION),"dawd   qeqawd","asfeefsefsfes",3.0,5.0));
        moviesReposotory.add(new Movie("T1-2", List.of(Genre.ADVENTURE),"dawdaawdadwdwawd","asfeefgsgsefees",2.0,2.0));
        moviesReposotory.add(new Movie("T3", List.of(Genre.COMEDY,Genre.DRAMA),"dawdawfgsffvxfdawd","asfdagseefes",2.6,7.01));
        // manual id
        for(Movie movie:moviesReposotory){
            movie.setId(id_counter++);
        }


    }




    @Override
    public Movie findMovieByTitle(int title) {
        for(Movie movie:moviesReposotory){
            if(movie.getTitle().equals(title)){
                return movie;
            }
        }
        return null;
    }

    @Override
    public List<Movie> findAllMovies() {
        return moviesReposotory;
    }

    @Override
    public Movie findMovieById(long id) {
        for(Movie movie:moviesReposotory){
            if(movie.getId()==id){
                return movie;
            }
        }
        return null;
    }

    @Override
    public Movie saveMovie(Movie movie) {
        movie.setId(id_counter++);
        moviesReposotory.add(movie);
        return movie;
    }

    @Override
    public void deleteMovie(Movie movie) {
        moviesReposotory.remove(movie);

    }
}
