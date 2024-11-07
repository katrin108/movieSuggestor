package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Services.implementation.TmdbServiceImplementation;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import jakarta.servlet.http.HttpSession; // Import HttpSession
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final UserService userService;
    private final TmdbServiceImplementation tmdbService;

    @Autowired
    public HomeController(UserService userService, TmdbServiceImplementation tmdbService) {
        this.userService = userService;
        this.tmdbService = tmdbService;
    }

    @RequestMapping("/")
    public String homePage(Model model, HttpSession session) { // Add HttpSession as a parameter


        // allir notendur
        List<User> allUsers = userService.findAllUsers();
        model.addAttribute("users", allUsers);
        model.addAttribute("genres", Genre.values());

        User sessionUser = (User) session.getAttribute("LoggedInUser"); // Retrieve the logged-in user
        if (sessionUser != null) {

            List<Map<String, Object>> personalizedMovies = userService.moviePreferenceSuggest(sessionUser);
            model.addAttribute("personalizedMovies", personalizedMovies);
        }

        return "home";
    }
    @PostMapping("/")
    public String getNewMoviePost(HttpSession session,
                              @RequestParam(value = "genres", required = false) List<Genre> selectedGenres
            , @RequestParam(value = "action") String action, Model model) {
        Map<String, Object> randomMovie=null;
        if("Random Movie".equals(action)) {
            randomMovie = tmdbService.getRandomPopularMovie();
        }
        else if("Movie based on selected genres".equals(action)) {
            if (selectedGenres != null) {
                randomMovie= tmdbService.getRandomPersonalizedMovie(selectedGenres);
            }
            else {
                randomMovie = tmdbService.getRandomPopularMovie();
            }
            System.out.println("Selected Genres: " + selectedGenres);
        }
        else if("Movie based on saved genres".equals(action)) {
            User sessionUser = (User) session.getAttribute("LoggedInUser");
            if (sessionUser != null) {
                randomMovie= tmdbService.getRandomPersonalizedMovie(sessionUser.getGenres());
            }
            else {
                return "redirect:/loggedin";
            }
        }
        else {
            randomMovie = tmdbService.getRandomPopularMovie();
        }
        model.addAttribute("tmdbMovie", randomMovie);
        model.addAttribute("movieGenre", tmdbService.getGenre(randomMovie));

        model.addAttribute("genres", Genre.values());
        model.addAttribute("selectedGenres", selectedGenres);

        return "home";
    }



}
