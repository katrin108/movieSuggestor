package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Services.TmdbService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tmdb")
public class TmdbController {

    private final TmdbService tmdbService;

    @Autowired
    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/random")
    public Map<String, Object> getRandomMovie(
            HttpSession session,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minVotes,
            @RequestParam(required = false) String certification,
            @RequestParam(required = false) Integer minRuntime,
            @RequestParam(required = false) Integer maxRuntime
    ) {
        User user = (User) session.getAttribute("LoggedInUser");
        Boolean childSafe = (user != null) ? user.getChild() : false;

        List<Genre> preferredGenres = getUserPreferredGenres(user);

        return tmdbService.getRandomPersonalizedMovie(
                preferredGenres,
                childSafe,
                minRating,
                minVotes,
                certification,
                minRuntime,
                maxRuntime
        );
    }


    private List<Genre> getUserPreferredGenres(User user) {
        if (user != null && user.getGenres() != null && !user.getGenres().isEmpty()) {
            return user.getGenres();
        } else {
            return null;
        }
    }
}
