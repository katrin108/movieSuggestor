package is.hi.hbv501g.moviesuggestor.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TasteDiveService {
    private final WebClient webClient;

    public TasteDiveService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://tastedive.com/api").build();
    }

    public List<String> getRecommendedMovies(String query) {
        try {
            String cleanedQuery = query.trim().replace(" ", "+");
            System.out.println("TasteDive API Request URL: https://tastedive.com/api/similar?q=" + cleanedQuery + "&type=movie&k=1039593-MovieSug-B6A0F246");

            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/similar")
                            .queryParam("q", cleanedQuery)
                            .queryParam("type", "movie")
                            .queryParam("k", "1039593-MovieSug-B6A0F246")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            System.out.println("TasteDive Response: " + response);

            @SuppressWarnings("unchecked")
            Map<String, Object> similar = (Map<String, Object>) response.get("similar");

            if (similar == null || !similar.containsKey("results")) {
                throw new RuntimeException("Invalid response structure from TasteDive API.");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) similar.get("results");

            return results.stream()
                    .map(result -> (String) result.get("name"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error fetching recommendations from TasteDive: " + e.getMessage());
            return List.of();
        }
    }
}
