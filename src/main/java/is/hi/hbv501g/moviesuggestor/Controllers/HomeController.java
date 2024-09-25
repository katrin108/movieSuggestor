package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import is.hi.hbv501g.moviesuggestor.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class HomeController {
    private MovieService movieService;

    @Autowired
    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }
    @RequestMapping("/")
    public String homePage(Model model) {
        //Call a method in Service Class
        List<Movie> allMovies =movieService.findAllMovies();

        //Add some data to the model
        model.addAttribute("movies", allMovies);
        return "home";
    }


    @RequestMapping(value = "/delete/{id}",method = RequestMethod.GET)
    public String deleteMovie(@PathVariable ("id") long id, Model model) {
        Movie movieToDelete=movieService.findMovieById(id);
        movieService.deleteMovie(movieToDelete);
        return "redirect:/";
    }
}
