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
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Skráningarsíða
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("genres", Genre.values()); // Tegundir
        return "signup";
    }

    // Skráning
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupPost(
            @ModelAttribute("user") User user,
            BindingResult result,
            Model model,
            @RequestParam(value = "genres", required = false) List<Genre> selectedGenres,
            HttpSession session
    ) {
        model.addAttribute("genres", Genre.values());

        if (result.hasErrors()) {
            return "signup"; // Endurhlaða ef villur
        }

        User existingUser = userService.findUserByUsername(user.getUsername());
        if (existingUser == null) {
            user.setGenres(selectedGenres != null ? selectedGenres : new ArrayList<>());
            userService.saveUser(user); // Vista notanda
            session.setAttribute("LoggedInUser", user); // Innskráning
            return "redirect:/"; // Áframsenda á heimasíðu
        }

        result.rejectValue("username", "username.exists", "Notandanafn er þegar til.");
        return "signup"; // Endurhlaða ef notandanafn til
    }

    // Innskráningarsíða
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGet(Model model) {
        model.addAttribute("user", new User()); // Tómt form
        return "login";
    }

    // Innskráning
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPost(
            @ModelAttribute("user") User user,
            BindingResult result,
            Model model,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            return "login"; // Endurhlaða ef villur
        }

        User existingUser = userService.login(user);
        if (existingUser != null) {
            session.setAttribute("LoggedInUser", existingUser); // Skráir inn
            return "redirect:/loggedin"; // Áframsenda á innskráða notandasíðu
        }

        model.addAttribute("loginError", "Rangt notandanafn eða lykilorð."); // Villuskilaboð
        return "login"; // Endurhlaða
    }

    // Sýnir innskráðan notanda
    @RequestMapping(value = "/loggedin", method = RequestMethod.GET)
    public String loggedInGet(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            model.addAttribute("genres", sessionUser.getGenres()); // Sýna tegundir
            return "loggedInUser";
        }

        return "redirect:/login"; // Ef ekki innskráður
    }

    // Útskráning
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logoutPost(HttpSession session) {
        session.invalidate(); // Afskrá notanda
        return "redirect:/"; // Áframsenda á heimasíðu
    }

    // Breyta tegundum
    @RequestMapping(value = "/loggedin/preferences", method = RequestMethod.GET)
    public String preferencesGet(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            model.addAttribute("UserGenres", sessionUser.getGenres());
            model.addAttribute("genres", Genre.values());
            return "preferences";
        }

        return "redirect:/loggedin"; // Ef ekki innskráður
    }

    // Breyta tegundum
    @RequestMapping(value = "/loggedin/preferences", method = RequestMethod.POST)
    public String preferencesPost(
            HttpSession session,
            @RequestParam(value = "genres", required = false) List<Genre> selectedGenres
    ) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            userService.setGenres(sessionUser, selectedGenres != null ? selectedGenres : new ArrayList<>());
        }
        return "redirect:/loggedin"; // Áframsenda á síðu skráðs notanda
    }

    // Sýnir alla notendur
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getAllUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "userList"; // Vistun notendalista
    }
}
