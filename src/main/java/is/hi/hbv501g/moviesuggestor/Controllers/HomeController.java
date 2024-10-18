package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Services.MovieService;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class HomeController {

    private final MovieService movieService;
    private final UserService userService;

    @Autowired
    public HomeController(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    // Heimasíða
    @RequestMapping("/")
    public String homePage(Model model, HttpSession session) {
        List<Movie> allMovies = movieService.findAllMovies();
        model.addAttribute("movies", allMovies);

        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("loggedInMessage", "You are logged in as: " + loggedInUser.getUsername());
            model.addAttribute("isLoggedIn", true); // Senda upplýsingum til að sýna rétt hnapp
        } else {
            model.addAttribute("loggedInMessage", "You are not logged in, welcome Guest.");
            model.addAttribute("isLoggedIn", false); // Senda upplýsingum til að sýna rétt hnapp
        }

        return "home";
    }

    // Ný mynd form
    @RequestMapping(value = "/addmovie", method = RequestMethod.GET)
    public String addMovieForm(Movie movie) {
        return "newMovie";
    }

    // Bæta við mynd
    @RequestMapping(value = "/addmovie", method = RequestMethod.POST)
    public String addMovie(@ModelAttribute Movie movie) {
        movieService.saveMovie(movie);
        return "redirect:/";
    }

    // Eyða mynd
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteMovie(@PathVariable("id") long id, Model model) {
        Movie movieToDelete = movieService.findMovieById(id);
        if (movieToDelete == null) {
            model.addAttribute("error", "Movie not found");
            return "error";
        }

        movieService.deleteMovie(movieToDelete);
        return "redirect:/";
    }

    // Sýna allar myndir
    @RequestMapping(value = "/movie", method = RequestMethod.GET)
    public String getAllMovies(Model model) {
        List<Movie> allMovies = movieService.findAllMovies();
        model.addAttribute("movies", allMovies);
        return "movieList";
    }
}
