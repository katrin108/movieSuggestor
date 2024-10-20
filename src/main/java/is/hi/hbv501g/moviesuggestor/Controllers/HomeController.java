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

    private UserService userService;


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String homePage(Model model) {



        List<User> allUsers =userService.findAllUsers();
        model.addAttribute("users", allUsers);

        //Add some data to the model

        return "home";
    }


}
