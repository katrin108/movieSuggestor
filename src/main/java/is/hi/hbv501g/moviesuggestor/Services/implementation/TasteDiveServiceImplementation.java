package is.hi.hbv501g.moviesuggestor.Services.Implementation;

import is.hi.hbv501g.moviesuggestor.Models.TasteDiveResponse;
import is.hi.hbv501g.moviesuggestor.Services.TasteDiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TasteDiveServiceImplementation implements TasteDiveService {

    private static final Logger log = LoggerFactory.getLogger(TasteDiveServiceImplementation.class);
    private final WebClient webClient;

    public TasteDiveServiceImplementation(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://tastedive.com/api").build();
    }

    @Override
    public List<String> getRecommendations(String query) {
        TasteDiveResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/similar")
                        .queryParam("q", query)
                        .queryParam("type", "movie")
                        .queryParam("k", "1039593-MovieSug-B6A0F246") // Replace with your API key
                        .build())
                .retrieve()
                .bodyToMono(TasteDiveResponse.class)
                .block();

        return response.getSimilar().getResults().stream().map(TasteDiveResponse.Result::getName).toList();
    }

    @Override
    public List<TasteDiveResponse.Info> getMovieInfo(String query) {
        TasteDiveResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/similar")
                        .queryParam("q", query)
                        .queryParam("type", "movie")
                        .queryParam("info", "1") // This fetches detailed info
                        .queryParam("k", "1039593-MovieSug-B6A0F246") // Replace with your API key
                        .build())
                .retrieve()
                .bodyToMono(TasteDiveResponse.class)
                .block();

        log.debug("[DEBUG] Fetched detailed movie info from TasteDive: {}", response.getSimilar().getInfo());
        return response.getSimilar().getInfo();
    }
}
