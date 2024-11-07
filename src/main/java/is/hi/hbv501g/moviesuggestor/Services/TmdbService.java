package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class TmdbService {
    private final WebClient webClient;

    @Value("${tmdb.api.key}")
    private String apiKey;

    public TmdbService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.themoviedb.org/3").build();
    }

    /* Fetches a random popular movie from TMDB API. */
    public Map<String, Object> getRandomPopularMovie() {
        try {
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
            System.err.println("Error fetching popular movies: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return null;
    }

    /* Fetches a random movie personalized based on user's preferred genres. */
    public Map<String, Object> getRandomPersonalizedMovie(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return getRandomPopularMovie();
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            Map<String, Object> response = fetchMoviesByGenresAndPage(genreIds, 1);
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

    /* Fetches personalized movie suggestions based on user's preferred genres. */
    public List<Map<String, Object>> getPersonalizedMovieSuggestions(List<Genre> genres) {
        List<Map<String, Object>> allResults = new ArrayList<>();

        if (genres == null || genres.isEmpty()) {
            return allResults;
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            for (int page = 1; page <= 5; page++) {
                Map<String, Object> response = fetchMoviesByGenresAndPage(genreIds, page);
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

    /* Fetches movies by genres, retrieving multiple pages of results. */
    public List<Map<String, Object>> getMoviesByGenres(List<Genre> genres) {
        List<Map<String, Object>> allResults = new ArrayList<>();

        if (genres == null || genres.isEmpty()) {
            return allResults;
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            for (int page = 1; page <= 5; page++) {
                Map<String, Object> response = fetchMoviesByGenresAndPage(genreIds, page);
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

    /* Fetches movie details from TMDB API based on a list of movie titles. */
    public List<Map<String, Object>> getMovieDetailsFromTitles(List<String> titles) {
        List<Map<String, Object>> movieDetailsList = new ArrayList<>();

        for (String title : titles) {
            try {
                Map<String, Object> response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/search/movie")
                                .queryParam("api_key", apiKey)
                                .queryParam("language", "en-US")
                                .queryParam("query", title)
                                .build())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

                if (results != null && !results.isEmpty()) {
                    movieDetailsList.add(results.get(0));
                }
            } catch (Exception e) {
                System.err.println("Error fetching details for title '" + title + "': " + e.getMessage());
            }
        }

        return movieDetailsList;
    }

    /* Private helper method to fetch movies by genres and a specific page. */
    private Map<String, Object> fetchMoviesByGenresAndPage(String genreIds, int page) {
        return webClient.get()
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
    }
}
