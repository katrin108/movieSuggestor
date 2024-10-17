package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //
    @RequestMapping(value = "/signup",method = RequestMethod.GET)
    public String signup(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("genres", Genre.values());
        return "signup";
    }

    @RequestMapping(value = "/signup",method = RequestMethod.POST)
    public String signupPost(@ModelAttribute("user") User user, BindingResult result, Model model, @RequestParam(value = "genres",required = false) List<Genre> selectedGenres, HttpSession session) {
        model.addAttribute("genres", Genre.values());
        if(result.hasErrors()) {
            return "signup";
        }
        User exists= userService.findUserByUsername(user.getUsername());
        if(exists == null) {
            user.setGenres(selectedGenres != null ? selectedGenres : new ArrayList<>());
            userService.saveUser(user);
            session.setAttribute("LoggedInUser", user);
            model.addAttribute("LoggedInUser", user);
            return "redirect:/loggedin";
        }
        else {
            model.addAttribute("user", exists);
            result.rejectValue("username", "username.exists");
        }
        return "signup";


    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGet(User user) {

        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPost(User user, BindingResult result, Model model, HttpSession session) {
        if(result.hasErrors()) {
            return "login";
        }
        User exists= userService.login(user);
        if(exists != null) {
            session.setAttribute("LoggedInUser", exists);
            model.addAttribute("LoggedInUser", exists);
            return "redirect:/loggedin";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/loggedin", method = RequestMethod.GET)
    public String loggedInGet(Model model, HttpSession session) {
        User sessionUser= (User) session.getAttribute("LoggedInUser");
        if(sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            return "loggedInUser";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }



}
