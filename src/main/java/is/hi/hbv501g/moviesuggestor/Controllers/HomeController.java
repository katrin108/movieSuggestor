package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Services.MovieService;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class HomeController {
    private MovieService movieService;
    private UserService userService;

    @Autowired
    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping("/")
    public String homePage(Model model) {
        //Call a method in Service Class
        List<Movie> allMovies =movieService.findAllMovies();

        List<User> allUsers =userService.findAllUsers();
        model.addAttribute("users", allUsers);

        //Add some data to the model
        model.addAttribute("movies", allMovies);
        return "home";
    }

    @RequestMapping(value = "/addmovie",method = RequestMethod.GET)
    public String addmovie(Movie movie) {

        return "newMovie";
    }

    @RequestMapping(value = "/addmovie",method = RequestMethod.POST)
    public String addMovie(Movie movie, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return "newMovie";
        }
        movieService.saveMovie(movie);
        return "redirect:/";
    }



    @RequestMapping(value = "/delete/{id}",method = RequestMethod.GET)
    public String deleteMovie(@PathVariable ("id") long id, Model model) {
        Movie movieToDelete=movieService.findMovieById(id);
        movieService.deleteMovie(movieToDelete);
        return "redirect:/";
    }
}
