package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;
import is.hi.hbv501g.moviesuggestor.Services.MovieListService;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    private MovieListService movieListService;
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
            user.setChild(false);
            user.setMovieLists(new ArrayList<MovieList>());
            user.setWatched(new Watched());
            userService.saveUser(user);
            session.setAttribute("LoggedInUser", user);
            model.addAttribute("LoggedInUser", user);

            return "redirect:/loggedin";
        }
        else {


            model.addAttribute("errorMessage","Username already exists");

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
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            model.addAttribute("genres", sessionUser.getGenres());
            model.addAttribute("movieLists", sessionUser.getMovieLists());
            model.addAttribute("watched", sessionUser.getWatched());

            Boolean showSettings=(Boolean) session.getAttribute("DivSettings");
            model.addAttribute("DivSettings", showSettings != null ? showSettings : false);
            return "loggedInUser";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    @RequestMapping(value = "/loggedin/preferences", method = RequestMethod.GET)
    public String preferencesGet(HttpSession session,@RequestParam(value = "genres",required = false) List<Genre> selectedGenres, Model model,@RequestParam(value = "action",required = false) String action) {
        User sessionUser= (User) session.getAttribute("LoggedInUser");
        if(sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            model.addAttribute("UserGenres", sessionUser.getGenres());
            model.addAttribute("genres", Genre.values());
            return "preferences";
        }
        return "redirect:/loggedin";

    }


        @RequestMapping(value = "/loggedin/preferences", method = RequestMethod.POST)
    public String preferencesPost(HttpSession session,@RequestParam(value = "genres",required = false) List<Genre> selectedGenres, Model model,@RequestParam(value = "action",required = false) String action) {

        User sessionUser= (User) session.getAttribute("LoggedInUser");
        if(sessionUser != null) {

            userService.setGenres(sessionUser,selectedGenres != null ? selectedGenres : new ArrayList<>());

        }
        return "redirect:/loggedin";
    }


    @RequestMapping(value = "/addMovieList",method = RequestMethod.POST)
    public String addMovieList(@RequestParam("name") String name,HttpSession session) {
        User sessionUser=(User) session.getAttribute("LoggedInUser");
        if(sessionUser != null) {
            MovieList newMovieList=new MovieList();
            newMovieList.setName(name);
            newMovieList.setUser(sessionUser);
            movieListService.saveMovieList(newMovieList);

            sessionUser.getMovieLists().add(newMovieList);

        }
        return "redirect:/loggedin";
    }

    @RequestMapping(value = "/deleteMovieList", method = RequestMethod.POST)
    public String deleteMovieList(@RequestParam("movieListId") long movieListId, HttpSession session,Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        MovieList movieList= movieListService.findMovieListById(movieListId);
        if (sessionUser != null && movieList != null) {

            movieListService.deleteMovieList(movieList);
            sessionUser.getMovieLists().removeIf(existingList -> existingList.getId() == movieListId);
            session.setAttribute("LoggedInUser", sessionUser);

        }

        return "redirect:/loggedin";
    }



    @RequestMapping(value = "/loggedin",method = RequestMethod.POST)
    public String userSettings( HttpSession session) {

        Boolean showSettings=(Boolean) session.getAttribute("DivSettings");

        if(showSettings==null){
            showSettings=false;
        }
        showSettings = !showSettings;

        session.setAttribute("DivSettings", showSettings);


        return "redirect:/loggedin";
    }
    @RequestMapping(value = "/saveSettings",method = RequestMethod.POST)
    public String saveSettings(HttpSession session, @RequestParam(value="child" ,required = false) Boolean child, @RequestParam(value = "username") String username, @RequestParam(value = "password") String password, @RequestParam(value = "email",required = false) String email, Model model) {

        Boolean showSettings=(Boolean) session.getAttribute("DivSettings");

        User sessionUser= (User) session.getAttribute("LoggedInUser");
        username= username != null ? username.trim():"";
        password= password != null ? password.trim():"";

        if(sessionUser != null) {
            if(!username.isEmpty() && !password.isEmpty() ){
                User exists= userService.findUserByUsername(username);
                if(exists == null||sessionUser.getUsername().equals(exists.getUsername())) {
                    if(child==null) {
                        child=false;
                    }
                    sessionUser.setChild(child);

                    sessionUser.setUsername(username);
                    sessionUser.setPassword(password);
                    sessionUser.setEmail(email);
                    userService.saveUser(sessionUser);


                    session.setAttribute("LoggedInUser", sessionUser);

                    showSettings = !showSettings;

                }
                else {
                    //user exists

                }

            }
            else {
                //you need a username and password
            }
        }


        session.setAttribute("DivSettings", showSettings);
        return "redirect:/loggedin";
    }

    @RequestMapping(value = "/deleteUser",method = RequestMethod.POST)
    public String deleteUser(HttpSession session) {
        User sessionUser= (User) session.getAttribute("LoggedInUser");
        if(sessionUser!=null) {
            userService.deleteUser(sessionUser);
            session.invalidate();
        }
        return "redirect:/loggedin";
    }





}
