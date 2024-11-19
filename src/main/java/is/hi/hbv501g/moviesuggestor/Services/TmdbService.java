package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import java.util.List;
import java.util.Map;

public interface TmdbService {
    Map<String, Object> getRandomPopularMovie(Boolean child);

    Map<String, Object> getRandomPersonalizedMovie(
            List<Genre> genres,
            Boolean child,
            Double minRating,
            Integer minVotes,
            String certification,
            Integer minRuntime,
            Integer maxRuntime
    );

    List<Map<String, Object>> getPersonalizedMovies(
            List<Genre> genres,
            Boolean child,
            Double minRating,
            Integer minVotes,
            String certification,
            Integer minRuntime,
            Integer maxRuntime
    );

    List<Map<String, Object>> getMoviesByGenres(List<Genre> genres, Boolean child);

    Map<String, Object> getMovieWithID(long id);

    List<Genre> getGenre(Map<String, Object> movie);

    List<Map<String, Object>> getMoviesFromTitles(List<String> titles);

    List<String> getRecommendedMovies(String query, Boolean child);
}
