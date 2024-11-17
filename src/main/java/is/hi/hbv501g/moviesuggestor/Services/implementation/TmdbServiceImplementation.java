package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Services.TmdbService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class TmdbServiceImplementation implements TmdbService {

    private final WebClient webClient;
    private final WebClient tasteDiveClient;

    @Value("${tmdb.api.key}")
    private String apiKey; // For TMDB API key

    @Value("${tastedive.api.key}")
    private String tasteDiveApiKey; // For TasteDive API key

    private static final String TASTEDIVE_BASE_URL = "https://tastedive.com/api";

    public TmdbServiceImplementation(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.themoviedb.org/3").build();
        this.tasteDiveClient = webClientBuilder.baseUrl(TASTEDIVE_BASE_URL).build();
    }

    /**
     * Calls TMDB and retrieves details of a movie based on a given ID.
     *
     * @param id Movie ID.
     * @return A map representing the movie details, or null if not found.
     */
    @Override
    public Map<String, Object> getMovieWithID(long id) {
        System.out.println("Made it to getMovieWithID: " + id);

        try {
            String uriString = UriComponentsBuilder.fromPath("/movie/{id}")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", "en-US")
                    .buildAndExpand(id)
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                System.out.println("Result is null");
            } else {
                return response;
            }
        } catch (Exception e) {
            System.err.println("Error fetching details for movie with id: '" + id + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Fetches a random popular movie from TMDB API.
     *
     * @param child Child-safe mode.
     * @return A map representing the movie details, or null if not found.
     */
    @Override
    public Map<String, Object> getRandomPopularMovie(Boolean child) {
        try {
            if (Boolean.TRUE.equals(child)) {
                return childUser();
            }

            String uriString = UriComponentsBuilder.fromPath("/movie/popular")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", "en-US")
                    .toUriString();

            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            int maxPages = totalPages != null ? Math.min(totalPages, 500) : 1;
            final int randomPage = new Random().nextInt(maxPages) + 1;

            uriString = UriComponentsBuilder.fromPath("/movie/popular")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", "en-US")
                    .queryParam("page", randomPage)
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uriString)
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
            System.err.println("Response body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Fetches a random movie suitable for children.
     *
     * @return A map representing the movie details, or null if not found.
     */
    public Map<String, Object> childUser() {
        try {
            List<Genre> genres = new ArrayList<>();
            genres.add(Genre.FAMILY);
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/discover/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", "en-US")
                    .queryParam("with_genres", genreIds)
                    .queryParam("certification_country", "US")
                    .queryParam("certification.lte", "PG");

            String uriString = uriBuilder.toUriString();

            // Fetch the initial response to get total pages
            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            int maxPages = totalPages != null ? Math.min(totalPages, 500) : 1;
            final int randomPage = new Random().nextInt(maxPages) + 1;

            // Update the URI with the random page
            uriBuilder.replaceQueryParam("page", randomPage);
            uriString = uriBuilder.toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results != null && !results.isEmpty()) {
                return results.get(new Random().nextInt(results.size()));
            }

        } catch (WebClientResponseException e) {
            System.err.println("Error fetching child-safe movies: " + e.getMessage());
            System.err.println("Response body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Fetches a list of movies personalized based on user's preferred genres and optional filters.
     *
     * @param genres        List of user's preferred genres.
     * @param child         Child-safe mode.
     * @param minRating     Minimum IMDb rating.
     * @param minVotes      Minimum number of votes.
     * @param certification Age rating (e.g., "PG-13").
     * @param minRuntime    Minimum runtime in minutes.
     * @param maxRuntime    Maximum runtime in minutes.
     * @return A map representing the movie details, or null if not found.
     */
    @Override
    public List<Map<String, Object>> getPersonalizedMovies(
            List<Genre> genres,
            Boolean child,
            Double minRating,
            Integer minVotes,
            String certification,
            Integer minRuntime,
            Integer maxRuntime
    ) {
        if (genres == null || genres.isEmpty()) {
            List<Map<String, Object>> result = new ArrayList<>();
            result.add(getRandomPopularMovie(child));
            return result;
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/discover/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", "en-US")
                    .queryParam("with_genres", genreIds);

            // Add optional filters if they are provided
            if (minRating != null) {
                uriBuilder.queryParam("vote_average.gte", minRating);
            }
            if (minVotes != null) {
                uriBuilder.queryParam("vote_count.gte", minVotes);
            }
            if (certification != null && !certification.isEmpty()) {
                uriBuilder.queryParam("certification_country", "US")
                        .queryParam("certification.lte", certification);
            }
            if (minRuntime != null) {
                uriBuilder.queryParam("with_runtime.gte", minRuntime);
            }
            if (maxRuntime != null) {
                uriBuilder.queryParam("with_runtime.lte", maxRuntime);
            }
            if (Boolean.TRUE.equals(child)) {
                uriBuilder.queryParam("certification_country", "US")
                        .queryParam("certification.lte", "PG");
            }

            // Build the URI string
            String uriString = uriBuilder.toUriString();

            // Fetch the initial response to get total pages
            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (initialResponse == null || initialResponse.isEmpty()) {
                System.err.println("TMDB API returned an empty or null response.");
                return null;
            }

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            if (totalPages == null || totalPages == 0) {
                System.err.println("No pages found for the given filters.");
                return null;
            }

            int maxPages = Math.min(totalPages, 500);
            final int randomPage = new Random().nextInt(maxPages) + 1;

            // Update the URI with the random page
            uriBuilder.replaceQueryParam("page", randomPage);
            uriString = uriBuilder.toUriString();

            System.out.println("Requesting URI: " + uriString);

            Map<String, Object> response = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results != null && !results.isEmpty()) {
                return results;
            } else {
                System.err.println("No movies found for the given filters.");
            }
        } catch (WebClientResponseException e) {
            System.err.println("Error fetching personalized movies: " + e.getMessage());
            System.err.println("Response body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Fetches a random movie personalized based on user's preferred genres and optional filters.
     *
     * @param genres        List of user's preferred genres.
     * @param child         Child-safe mode.
     * @param minRating     Minimum IMDb rating.
     * @param minVotes      Minimum number of votes.
     * @param certification Age rating (e.g., "PG-13").
     * @param minRuntime    Minimum runtime in minutes.
     * @param maxRuntime    Maximum runtime in minutes.
     * @return A map representing the movie details, or null if not found.
     */
    @Override
    public Map<String, Object> getRandomPersonalizedMovie(
            List<Genre> genres,
            Boolean child,
            Double minRating,
            Integer minVotes,
            String certification,
            Integer minRuntime,
            Integer maxRuntime
    ) {
        if (genres == null || genres.isEmpty()) {
            return getRandomPopularMovie(child);
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/discover/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", "en-US")
                    .queryParam("with_genres", genreIds);

            // Add optional filters if they are provided
            if (minRating != null) {
                uriBuilder.queryParam("vote_average.gte", minRating);
            }
            if (minVotes != null) {
                uriBuilder.queryParam("vote_count.gte", minVotes);
            }
            if (certification != null && !certification.isEmpty()) {
                uriBuilder.queryParam("certification_country", "US")
                        .queryParam("certification.lte", certification);
            }
            if (minRuntime != null) {
                uriBuilder.queryParam("with_runtime.gte", minRuntime);
            }
            if (maxRuntime != null) {
                uriBuilder.queryParam("with_runtime.lte", maxRuntime);
            }
            if (Boolean.TRUE.equals(child)) {
                uriBuilder.queryParam("certification_country", "US")
                        .queryParam("certification.lte", "PG");
            }

            // Build the URI string
            String uriString = uriBuilder.toUriString();

            // Fetch the initial response to get total pages
            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (initialResponse == null || initialResponse.isEmpty()) {
                System.err.println("TMDB API returned an empty or null response.");
                return null;
            }

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            if (totalPages == null || totalPages == 0) {
                System.err.println("No pages found for the given filters.");
                return null;
            }

            int maxPages = Math.min(totalPages, 500);
            final int randomPage = new Random().nextInt(maxPages) + 1;

            // Update the URI with the random page
            uriBuilder.replaceQueryParam("page", randomPage);
            uriString = uriBuilder.toUriString();

            System.out.println("Requesting URI: " + uriString);

            Map<String, Object> response = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results != null && !results.isEmpty()) {
                return results.get(new Random().nextInt(results.size()));
            } else {
                System.err.println("No movies found for the given filters.");
            }
        } catch (WebClientResponseException e) {
            System.err.println("Error fetching personalized movies: " + e.getMessage());
            System.err.println("Response body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return null;
    }



    /**
     * Fetches movies by genres, retrieving multiple pages of results.
     *
     * @param genres List of genres to filter by.
     * @param child  Child-safe mode.
     * @return A list of maps representing movie details.
     */
    @Override
    public List<Map<String, Object>> getMoviesByGenres(List<Genre> genres, Boolean child) {
        List<Map<String, Object>> allResults = new ArrayList<>();

        if (genres == null || genres.isEmpty()) {
            return allResults;
        }

        try {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getTmdbId()))
                    .collect(Collectors.joining(","));

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/discover/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", "en-US")
                    .queryParam("with_genres", genreIds);

            if (Boolean.TRUE.equals(child)) {
                uriBuilder.queryParam("certification_country", "US")
                        .queryParam("certification.lte", "PG");
            }

            String uriString = uriBuilder.toUriString();

            Map<String, Object> initialResponse = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Integer totalPages = (Integer) initialResponse.get("total_pages");
            int maxPages = totalPages != null ? Math.min(totalPages, 500) : 1;
            int pagesToFetch = Math.min(maxPages, 5); // Limit to first 5 pages

            for (int currentPage = 1; currentPage <= pagesToFetch; currentPage++) {
                uriBuilder.replaceQueryParam("page", currentPage);
                uriString = uriBuilder.toUriString();

                Map<String, Object> response = webClient.get()
                        .uri(uriString)
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
            System.err.println("Response body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return allResults;
    }

    /**
     * Extracts genres from a movie.
     *
     * @param movie Movie data.
     * @return List of genres.
     */
    @Override
    public List<Genre> getGenre(Map<String, Object> movie) {
        if (movie == null) {
            return Collections.emptyList();
        }

        List<Genre> genres = new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<Integer> genreIds = (List<Integer>) movie.get("genre_ids");

        if (genreIds != null && !genreIds.isEmpty()) {
            for (Integer genreId : genreIds) {
                try {
                    Genre genre = Genre.fromTmdbId(genreId);
                    genres.add(genre);
                } catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getMessage());
                }
            }
        } else {
            // For detailed movie data, genres might be a list of maps
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> genreMaps = (List<Map<String, Object>>) movie.get("genres");
            if (genreMaps != null && !genreMaps.isEmpty()) {
                for (Map<String, Object> genreMap : genreMaps) {
                    Integer genreId = (Integer) genreMap.get("id");
                    try {
                        Genre genre = Genre.fromTmdbId(genreId);
                        genres.add(genre);
                    } catch (Exception e) {
                        System.err.println("Unexpected error: " + e.getMessage());
                    }
                }
            }
        }
        return genres;
    }

    /**
     * Fetches movie details from TMDB API based on a list of movie titles.
     *
     * @param titles List of movie titles.
     * @return List of movie details.
     */
    @Override
    public List<Map<String, Object>> getMovieDetailsFromTitles(List<String> titles) {
        List<Map<String, Object>> movieDetailsList = new ArrayList<>();

        for (String title : titles) {
            try {
                String uriString = UriComponentsBuilder.fromPath("/search/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "en-US")
                        .queryParam("query", title)
                        .toUriString();

                Map<String, Object> response = webClient.get()
                        .uri(uriString)
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

    /**
     * Fetches recommended movies based on a query.
     *
     * @param query User's query.
     * @param child Child-safe mode.
     * @return List of recommended movie titles.
     */
    @Override
    public List<String> getRecommendedMovies(String query, Boolean child) {
        try {
            String cleanedQuery = query.trim().replace(" ", "+");
            if (cleanedQuery.isEmpty()) {
                throw new IllegalArgumentException("Query cannot be empty or null");
            }

            Map<String, Object> response;

            if (Boolean.TRUE.equals(child)) {
                // Implement child-safe logic if needed
                Map<String, Object> movie = childUser();
                // Since childUser returns a random movie, adjust logic accordingly
                if (movie != null && movie.containsKey("title")) {
                    String title = (String) movie.get("title");
                    return List.of(title);
                } else {
                    throw new RuntimeException("No child-safe recommendations available.");
                }
            } else {
                String uriString = UriComponentsBuilder.fromHttpUrl(TASTEDIVE_BASE_URL + "/similar")
                        .queryParam("q", cleanedQuery)
                        .queryParam("type", "movies")
                        .queryParam("k", tasteDiveApiKey)
                        .toUriString();

                response = tasteDiveClient.get()
                        .uri(uriString)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response != null && response.containsKey("Similar")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> similar = (Map<String, Object>) response.get("Similar");
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> results = (List<Map<String, String>>) similar.get("Results");

                    return results.stream()
                            .map(movie -> movie.get("Name"))
                            .limit(5)
                            .collect(Collectors.toList());
                } else {
                    throw new RuntimeException("Invalid response structure from TasteDive API");
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching recommended movies: " + e.getMessage());
            return List.of("No recommendations available. Please try again later.");
        }
    }
}
