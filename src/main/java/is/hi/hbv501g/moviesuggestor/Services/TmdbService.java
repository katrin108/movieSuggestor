package is.hi.hbv501g.moviesuggestor.Services;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TmdbService {
    Map<String,Object> getRandomPopularMovie(User user, Boolean child);


    Map<String,Object> getRandomPersonalizedMovie(User user,List<Genre> genres, Boolean child);
    List<Map<String, Object>> getPersonalizedMovieSuggestions(User user,List<Genre> genres, Boolean child);
    List<Map<String, Object>> getMoviesByGenres(User user,List<Genre> genres, Boolean child);

    Map<String,Object> getMovieWithID(long id);

    List<Genre> getGenre(Map<String, Object> movie);
    List<Map<String, Object>> getMovieDetailsFromTitles(List<String> titles);
    List<String> getRecommendedMovies(User user,String query, Boolean child);

}