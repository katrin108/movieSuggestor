package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Services.TmdbService;
import org.apache.catalina.connector.Request;
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
public class TmdbServiceImplementation implements TmdbService {
    private final WebClient webClient;
    private static final String BASE_URL = "https://tastedive.com/api";

    @Value("${tmdb.api.key}")
    private String apiKey;

    private static final String API_KEY = "1039593-MovieSug-B6A0F246";

    public TmdbServiceImplementation(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.themoviedb.org/3").build();
    }

    /**
     * Calls the TMDB and retrieves details of a movie based on a given ID
     * @param id
     * @return
     */
    public Map<String,Object> getMovieWithID(long id) {
        System.out.println("Made it to getMovieWith ID: " + id);

        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/" + id)
                            .queryParam("movie_id", id)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Map<String, Object> results = (Map<String, Object>) response.get("results");
            if (results == null) { System.out.println("Result is null"); }
            if (results != null /*&& !results.isEmpty()*/) {
                //Map<String, Object> results = results.get(0);
                for (String key : results.keySet()) {
                    System.out.println(key);
                }
                return results;
            }
            } catch (Exception e) {
                System.err.println("Error fetching details for movie with id: '" + id + "': " + e.getMessage());
            }
        return null;
    }

    /**
     * Fetches a random popular movie from TMDB API.
     *
     * @return A map representing the movie details, or null if not found.
     */
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

    public List<Genre> getGenre(Map<String, Object> movie){
        List<Genre> genres = new ArrayList<>();

        @SuppressWarnings("unchecked")
                List<Integer> genreIds = (List<Integer>) movie.get("genre_ids");

        if (genreIds != null && !genreIds.isEmpty()) {
            for (Integer genreId : genreIds) {
                try {
                    Genre genre=Genre.fromTmdbId(genreId);
                    genres.add(genre);
                }
                catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getMessage());
                }
            }
        }
        return genres;
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

    public List<String> getRecommendedMovies(String query) {
        try {
            String cleanedQuery = query.trim().replace(" ", "+");
            if (cleanedQuery.isEmpty()) {
                throw new IllegalArgumentException("Query cannot be empty or null");
            }

            String requestUrl = BASE_URL + "/similar?q=" + cleanedQuery + "&type=movie&k=" + API_KEY;

            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/similar")
                            .queryParam("q", cleanedQuery)
                            .queryParam("type", "movie")
                            .queryParam("k", API_KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("similar")) {
                Map<String, Object> similar = (Map<String, Object>) response.get("similar");
                List<Map<String, String>> results = (List<Map<String, String>>) similar.get("results");

                return results.stream()
                        .map(movie -> movie.get("name"))
                        .limit(5)
                        .collect(Collectors.toList());
            } else {
                throw new RuntimeException("Invalid response structure from TasteDive API");
            }
        } catch (Exception e) {
            return List.of("No recommendations available. Please try again later.");
        }
    }
}
