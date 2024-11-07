package is.hi.hbv501g.moviesuggestor.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TasteDiveService {
    private final WebClient webClient;
    private static final String API_KEY = "1039593-MovieSug-B6A0F246";
    private static final String BASE_URL = "https://tastedive.com/api";

    public TasteDiveService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
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
