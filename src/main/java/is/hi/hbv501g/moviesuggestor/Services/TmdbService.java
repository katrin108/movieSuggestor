package is.hi.hbv501g.moviesuggestor.Services;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TmdbService {
    Map<String,Object> getRandomPopularMovie( User user);

    Map<String,Object> getRandomPersonalizedMovie(List<Genre> genres, User user);
    List<Map<String, Object>> getPersonalizedMovieSuggestions(List<Genre> genres);
    List<Map<String, Object>> getMoviesByGenres(List<Genre> genres, User user);
    List<Genre> getGenre(Map<String, Object> movie);
    List<Map<String, Object>> getMovieDetailsFromTitles(List<String> titles, User user);
    List<String> getRecommendedMovies(String query, User user);

}