package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Services.TmdbService;
import is.hi.hbv501g.moviesuggestor.Services.implementation.TmdbServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TmdbController {

    private final TmdbService tmdbService;

    @Autowired
    public TmdbController(TmdbServiceImplementation tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/tmdb/random")
    public Map<String, Object> getRandomMovie() {
        return tmdbService.getRandomPopularMovie();
    }
}
