package is.hi.hbv501g.moviesuggestor.Services;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TmdbService {
    Map<String,Object> getRandomPopularMovie();

    Map<String,Object> getRandomPersonalizedMovie(List<Genre> genres);
    List<Map<String, Object>> getPersonalizedMovieSuggestions(List<Genre> genres);
    List<Map<String, Object>> getMoviesByGenres(List<Genre> genres);
    List<Genre> getGenre(Map<String, Object> movie);
    List<Map<String, Object>> getMovieDetailsFromTitles(List<String> titles);
    List<String> getRecommendedMovies(String query);

}