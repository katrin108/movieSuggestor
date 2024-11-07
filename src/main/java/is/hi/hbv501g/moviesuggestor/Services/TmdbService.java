package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class TmdbService {
    private final WebClient webClient;

    @Value("${tmdb.api.key}")
    private String apiKey;

    public TmdbService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.themoviedb.org/3").build();
    }

    /**
     * Fetches a random popular movie from TMDB API.
     *
     * @return A map representing the movie details, or null if not found.
     */
    public Map<String, Object> getRandomPopularMovie() {
        try {
            // Fetch initial response to get total pages
            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/popular")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            int maxPages = totalPages != null ? Math.min(totalPages, 500) : 1;
            final int randomPage = new Random().nextInt(maxPages) + 1;

            // Fetch movies from the random page
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/popular")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("page", randomPage)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results != null && !results.isEmpty()) {
                return results.get(new Random().nextInt(results.size()));
            }
        } catch (WebClientResponseException e) {
            // Handle client-side errors
            System.err.println("Error fetching popular movies: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Fetches a random movie personalized based on user's preferred genres.
     *
     * @param genres List of user's preferred genres.
     * @return A map representing the movie details, or null if not found.
     */
    public Map<String, Object> getRandomPersonalizedMovie(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return getRandomPopularMovie();
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            // Fetch initial response to get total pages
            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/discover/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("with_genres", genreIds)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            int maxPages = totalPages != null ? Math.min(totalPages, 500) : 1;
            final int randomPage = new Random().nextInt(maxPages) + 1;

            // Fetch movies from the random page
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/discover/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("with_genres", genreIds)
                            .queryParam("page", randomPage)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results != null && !results.isEmpty()) {
                return results.get(new Random().nextInt(results.size()));
            }
        } catch (WebClientResponseException e) {
            System.err.println("Error fetching personalized movies: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Fetches personalized movie suggestions based on user's preferred genres.
     *
     * @param genres List of user's preferred genres.
     * @return A list of maps representing movie details.
     */
    public List<Map<String, Object>> getPersonalizedMovieSuggestions(List<Genre> genres) {
        List<Map<String, Object>> allResults = new ArrayList<>();

        if (genres == null || genres.isEmpty()) {
            return allResults;
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            // Fetch initial response to get total pages
            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/discover/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("with_genres", genreIds)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            int maxPages = totalPages != null ? Math.min(totalPages, 500) : 1;
            int pagesToFetch = Math.min(maxPages, 5); // Limit to first 5 pages

            for (int currentPage = 1; currentPage <= pagesToFetch; currentPage++) {
                final int page = currentPage; // Declare as final to use inside lambda

                Map<String, Object> response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/discover/movie")
                                .queryParam("api_key", apiKey)
                                .queryParam("language", "en-US")
                                .queryParam("with_genres", genreIds)
                                .queryParam("page", page)
                                .build())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                if (results != null) {
                    allResults.addAll(results);
                }
            }
        } catch (WebClientResponseException e) {
            System.err.println("Error fetching movie suggestions: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return allResults;
    }

    /**
     * Fetches movies by genres, retrieving multiple pages of results.
     *
     * @param genres List of genres to filter by.
     * @return A list of maps representing movie details.
     */
    public List<Map<String, Object>> getMoviesByGenres(List<Genre> genres) {
        List<Map<String, Object>> allResults = new ArrayList<>();

        if (genres == null || genres.isEmpty()) {
            return allResults;
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            // Fetch initial response to get total pages
            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/discover/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("with_genres", genreIds)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            int maxPages = totalPages != null ? Math.min(totalPages, 500) : 1;
            int pagesToFetch = Math.min(maxPages, 5); // Limit to first 5 pages

            for (int currentPage = 1; currentPage <= pagesToFetch; currentPage++) {
                final int page = currentPage; // Declare as final to use inside lambda

                Map<String, Object> response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/discover/movie")
                                .queryParam("api_key", apiKey)
                                .queryParam("language", "en-US")
                                .queryParam("with_genres", genreIds)
                                .queryParam("page", page)
                                .build())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                if (results != null) {
                    allResults.addAll(results);
                }
            }
        } catch (WebClientResponseException e) {
            System.err.println("Error fetching movies by genres: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return allResults;
    }
}
