package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Services.TmdbService;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import jakarta.servlet.http.HttpSession; // Import HttpSession
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String homePage(Model model, HttpSession session) { // Add HttpSession as a parameter
        // random mynd frá tmdb
        Map<String, Object> randomMovie = tmdbService.getRandomPopularMovie();
        model.addAttribute("tmdbMovie", randomMovie);

        // allir notendur
        List<User> allUsers = userService.findAllUsers();
        model.addAttribute("users", allUsers);


        User sessionUser = (User) session.getAttribute("LoggedInUser"); // Retrieve the logged-in user
        if (sessionUser != null) {

            List<Map<String, Object>> personalizedMovies = userService.moviePreferenceSuggest(sessionUser);
            model.addAttribute("personalizedMovies", personalizedMovies);
        }

        return "home";
    }
}
