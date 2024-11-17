package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import is.hi.hbv501g.moviesuggestor.Services.TmdbService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final UserService userService;
    private final TmdbService tmdbService;

    @Autowired
    public HomeController(UserService userService, TmdbService tmdbService) {
        this.userService = userService;
        this.tmdbService = tmdbService;
    }

    @RequestMapping("/")
    public String homePage(Model model, HttpSession session) {
        // All users
        List<User> allUsers = userService.findAllUsers();
        User sessionUser = (User) session.getAttribute("LoggedInUser");

        model.addAttribute("users", allUsers);
        model.addAttribute("genres", Genre.values());

        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            List<Map<String, Object>> personalizedMovies = tmdbService.getMoviesByGenres(sessionUser.getGenres(),sessionUser.getChild());
            
            model.addAttribute("personalizedMovies", personalizedMovies);
        }

        return "home";
    }

    @PostMapping("/")
    public String getNewMoviePost(
            HttpSession session,
            @RequestParam(value = "genres", required = false) List<Genre> selectedGenres,
            @RequestParam(value = "action") String action,
            @RequestParam(value = "child_safe", required = false) Boolean child_safe,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minVotes,
            @RequestParam(required = false) String certification,
            @RequestParam(required = false) Integer minRuntime,
            @RequestParam(required = false) Integer maxRuntime,
            Model model) {

        Map<String, Object> randomMovie = null;

        User sessionUser = (User) session.getAttribute("LoggedInUser");
        Boolean child = child_safe != null ? child_safe : false;
        if (!child && sessionUser != null) {
            child = Boolean.TRUE.equals(sessionUser.getChild());
        }

        if ("Random Movie".equals(action)) {
            randomMovie = tmdbService.getRandomPopularMovie(child);
        } else if ("Movie based on selected genres".equals(action)) {
            if (selectedGenres != null && !selectedGenres.isEmpty()) {
                randomMovie = tmdbService.getRandomPersonalizedMovie(
                        selectedGenres,
                        child,
                        minRating,
                        minVotes,
                        certification,
                        minRuntime,
                        maxRuntime
                );
            } else {
                randomMovie = tmdbService.getRandomPopularMovie(child);
            }
            System.out.println("Selected Genres: " + selectedGenres);
        } else if ("Movie based on saved genres".equals(action)) {
            if (sessionUser != null && sessionUser.getGenres() != null && !sessionUser.getGenres().isEmpty()) {
                randomMovie = tmdbService.getRandomPersonalizedMovie(
                        sessionUser.getGenres(),
                        child,
                        minRating,
                        minVotes,
                        certification,
                        minRuntime,
                        maxRuntime
                );
            } else {
                randomMovie = tmdbService.getRandomPopularMovie(child);
            }
        } else {
            randomMovie = tmdbService.getRandomPopularMovie(child);
        }

        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
        }
        model.addAttribute("child_safe", child);

        if (randomMovie != null) {
            model.addAttribute("tmdbMovie", randomMovie);
            model.addAttribute("movieGenre", tmdbService.getGenre(randomMovie));
        } else {
            model.addAttribute("errorMessage", "No movies found matching your criteria.");
        }

        model.addAttribute("genres", Genre.values());
        model.addAttribute("selectedGenres", selectedGenres);

        return "home";
    }
}
