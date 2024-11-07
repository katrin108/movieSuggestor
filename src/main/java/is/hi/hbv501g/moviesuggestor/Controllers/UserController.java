package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Services.TasteDiveService;
import is.hi.hbv501g.moviesuggestor.Services.MovieListService;
import is.hi.hbv501g.moviesuggestor.Services.TmdbService;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private final UserService userService;
    private final TmdbService tmdbService;
    private final TasteDiveService tasteDiveService;

    @Autowired
    public UserController(UserService userService, TmdbService tmdbService, TasteDiveService tasteDiveService) {
        this.userService = userService;
        this.tmdbService = tmdbService;
        this.tasteDiveService = tasteDiveService;
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("genres", Genre.values());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupPost(@ModelAttribute("user") User user, BindingResult result, Model model,
                             @RequestParam(value = "genres", required = false) List<Genre> selectedGenres,
                             HttpSession session) {
        model.addAttribute("genres", Genre.values());
        if (result.hasErrors()) {
            return "signup";
        }
        User exists = userService.findUserByUsername(user.getUsername());
        if (exists == null) {
            user.setGenres(selectedGenres != null ? selectedGenres : new ArrayList<>());
            user.setChild(false);
            user.setMovieLists(new ArrayList<>());

            userService.saveUser(user);
            session.setAttribute("LoggedInUser", user);
            model.addAttribute("LoggedInUser", user);
            return "redirect:/loggedin";
        } else {
            model.addAttribute("errorMessage", "Username already exists");
            result.rejectValue("username", "username.exists");
            return "signup";
        }
    }

    @GetMapping("/login")
    public String loginGet(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute("user") User user, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }
        User exists = userService.login(user);
        if (exists != null) {
            session.setAttribute("LoggedInUser", exists);
            model.addAttribute("LoggedInUser", exists);
            return "redirect:/loggedin";
        } else {
            model.addAttribute("errorMessage", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/loggedin")
    public String loggedInGet(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            User loggedInUser = userService.findUserById(sessionUser.getId());
            model.addAttribute("LoggedInUser", loggedInUser);
            model.addAttribute("genres", loggedInUser.getGenres());
            model.addAttribute("movieLists", loggedInUser.getMovieLists());
            model.addAttribute("recommendedMovie", null);
            model.addAttribute("hasSuggestedMovie", false);
            Boolean showSettings = (Boolean) session.getAttribute("DivSettings");
            model.addAttribute("DivSettings", showSettings != null ? showSettings : false);
            return "loggedInUser";
        }
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/getSuggestedMovie")
    public String getSuggestedMovie(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            User loggedInUser = userService.findUserById(sessionUser.getId());

            List<Genre> userGenres = loggedInUser.getGenres();
            Map<String, Object> recommendedMovie;
            if (userGenres != null && !userGenres.isEmpty()) {
                recommendedMovie = tmdbService.getRandomPersonalizedMovie(userGenres);
            } else {
                recommendedMovie = tmdbService.getRandomPopularMovie();
            }
            model.addAttribute("recommendedMovie", recommendedMovie);
            model.addAttribute("hasSuggestedMovie", recommendedMovie != null);
            model.addAttribute("LoggedInUser", loggedInUser);
            model.addAttribute("genres", loggedInUser.getGenres());
            model.addAttribute("movieLists", loggedInUser.getMovieLists());
            Boolean showSettings = (Boolean) session.getAttribute("DivSettings");
            model.addAttribute("DivSettings", showSettings != null ? showSettings : false);
        }
        return "loggedInUser";
    }

    /**
     * Endpoint to handle movie recommendations based on a user-input sentence.
     *
     * @param query The input sentence describing the type of movie the user wants.
     * @param model The model to pass data to the Thymeleaf template.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/api/movies/recommend")
    public String recommendMovies(@RequestParam String query, Model model) {
        try {

            List<String> recommendedTitles = tasteDiveService.getRecommendedMovies(query);


            List<Map<String, Object>> recommendedMovies = tmdbService.getMovieDetailsFromTitles(recommendedTitles);

            model.addAttribute("recommendedMovies", recommendedMovies);
            model.addAttribute("query", query);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to fetch recommendations. Please try again later.");
            e.printStackTrace();
        }
        return "home";
    }
}
