package is.hi.hbv501g.moviesuggestor.Services;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TmdbService {
    Map<String,Object> getRandomPopularMovie( Boolean child);

    Map<String,Object> getRandomPersonalizedMovie(List<Genre> genres, Boolean child);
    List<Map<String, Object>> getPersonalizedMovieSuggestions(List<Genre> genres, Boolean child);
    List<Map<String, Object>> getMoviesByGenres(List<Genre> genres, Boolean child);
    List<Genre> getGenre(Map<String, Object> movie);
    List<Map<String, Object>> getMovieDetailsFromTitles(List<String> titles, Boolean child);
    List<String> getRecommendedMovies(String query, Boolean child);

}