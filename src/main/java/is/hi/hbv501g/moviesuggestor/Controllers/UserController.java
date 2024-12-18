package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.*;
import is.hi.hbv501g.moviesuggestor.Services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class UserController {
    @Autowired
    private final UserService userService;

    private final TmdbService tmdbService;
    private final TasteDiveService tasteDiveService;
    private final MovieListService movieListService;
    private final WatchedService watchedService;

    @Autowired
    public UserController(UserService userService, TmdbService tmdbService, TasteDiveService tasteDiveService, MovieListService movieListService, WatchedService watchedService) {
        this.userService = userService;
        this.tmdbService = tmdbService;
        this.tasteDiveService = tasteDiveService;
        this.movieListService = movieListService;
        this.watchedService = watchedService;
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
            Watched watched = new Watched();


            watched.setUser(user);
            user.setWatched(watched);


            userService.saveUser(user);
            session.setAttribute("LoggedInUser", user);

            model.addAttribute("watchedMovies", user.getWatched().getMovies());

            model.addAttribute("LoggedInUser", user);

            model.addAttribute("recommendedMovies", new ArrayList<>());

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
            model.addAttribute("Usergenres", loggedInUser.getGenres());
            model.addAttribute("movieLists", loggedInUser.getMovieLists());
            model.addAttribute("watchedMovies", loggedInUser.getWatched().getMovies());
            model.addAttribute("recommendedMovie", null);
            model.addAttribute("hasSuggestedMovie", false);
            Boolean showSettings = (Boolean) session.getAttribute("DivSettings");
            model.addAttribute("DivSettings", showSettings != null ? showSettings : false);

            model.addAttribute("genres", Genre.values());
            model.addAttribute("totalTime",loggedInUser.getTotalTime());

            model.addAttribute("recommendedMovies", new ArrayList<>());

            return "loggedInUser";
        }
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/loggedin/preferences")
    public String preferencesGet(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            User loggedInUser = userService.findUserById(sessionUser.getId());

            model.addAttribute("LoggedInUser", loggedInUser);
            model.addAttribute("UserGenres", loggedInUser.getGenres());
            model.addAttribute("genres", Genre.values());
            return "preferences";
        }
        return "redirect:/login";
    }

    @PostMapping("/loggedin/preferences")
    public String preferencesPost(HttpSession session,
                                  @RequestParam(value = "genres", required = false) List<Genre> selectedGenres,
                                  @RequestParam(value = "action") String action, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            if ("Clear Preferences".equals(action)) {
                userService.setGenres(sessionUser, new ArrayList<>());
            }
            if ("Save Preferences".equals(action)) {
                userService.setGenres(sessionUser, selectedGenres != null ? selectedGenres : new ArrayList<>());
            }
        }
        return "redirect:/loggedin";
    }

    @PostMapping("/removeAMovieFromWatched")
    public String removeAMovieFromWatched(@RequestParam("movieId") long movieId,
                                          @RequestParam("userId") long userId,
                                          Model model, HttpSession session) {
        User loggedInUser = userService.findUserById(userId);
        Watched watched=loggedInUser.getWatched();
        if(watched == null||loggedInUser==null) {
            return "redirect:/loggedin";
        }

        for(Movie m:watched.getMovies()) {
            if(m.getId()==movieId) {
                watched.removeMovie(m);
                watchedService.saveWatched(watched);
                session.setAttribute("LoggedInUser", userService.findUserById(loggedInUser.getId()));
                model.addAttribute("LoggedInUser", loggedInUser);
                model.addAttribute("watchedMovies", watched.getMovies());
                model.addAttribute("watched",watched);
                model.addAttribute("movieLists",loggedInUser.getMovieLists());
                break;
            }
        }

        return "watched";
    }

    @GetMapping("/loggedIn/watched")
    public String viewWatched(HttpSession session, Model model) {
        User user = (User) session.getAttribute("LoggedInUser");
        if(user==null) {
            return "redirect:/loggedin";
        }

        Watched watched=user.getWatched();

        if(watched==null) {
            return "redirect:/loggedin";
        }

        model.addAttribute("LoggedInUser", user);
        model.addAttribute("watchedMovies", watched.getMovies());
        model.addAttribute("watched",watched);
        model.addAttribute("movieLists",user.getMovieLists());

        return "watched";
    }


    @PostMapping("/addMovieList")
    public String addMovieList(@RequestParam("name") String name, HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            MovieList newMovieList = new MovieList();
            newMovieList.setName(name);
            newMovieList.setUser(sessionUser);
            movieListService.saveMovieList(newMovieList);
            sessionUser.getMovieLists().add(newMovieList);
        }
        return "redirect:/loggedin";
    }



    @GetMapping("/loggedIn/movieList")
    public String viewMoviesList(@RequestParam("listId") long listId,HttpSession session, Model model) {
        User user = (User) session.getAttribute("LoggedInUser");
        if(user==null) {
            return "redirect:/loggedin";
        }
        MovieList movieList=movieListService.findMovieListById(listId);

        if(movieList==null) {
            return "redirect:/loggedin";
        }

        model.addAttribute("LoggedInUser", user);
        model.addAttribute("movieList", movieList);
        return "movieList";
    }

    @PostMapping("/removeAMovieFromMovieList")
    public String removeAMovieFromMovieList(@RequestParam("movieId") long movieId,@RequestParam("movieListId") long ListId, HttpSession session, Model model){
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        MovieList movieList=loggedInUser.getMovieList(ListId);

        if(loggedInUser==null||movieList==null) {
            return "redirect:/loggedin";
        }
        for (Movie m:movieList.getMovies()) {
            if (m.getId() == movieId) {
                movieList.getMovies().remove(m);
                movieListService.saveMovieList(movieList);
                break;
            }
        }

        if(movieList.getMovies().isEmpty()) {
            return "redirect:/loggedin";
        }

        session.setAttribute("LoggedInUser", userService.findUserById(loggedInUser.getId()));
        model.addAttribute("LoggedInUser", loggedInUser);
        model.addAttribute("movieList", movieList);
        return "movieList";
    }

    @PostMapping("/getAMovieFromMovieList")
    public String getAMovieFromMovieList(@RequestParam("movieListId") long ListId, HttpSession session, Model model){
        User user = (User) session.getAttribute("LoggedInUser");
        if(user==null) {
            return "redirect:/loggedin";
        }
        MovieList movieList=user.getMovieList(ListId);
        if(movieList==null||movieList.getMovies().isEmpty()) {
            return "redirect:/loggedin";
        }
        Random random=new Random();

        Movie movie=movieList.getMovies().get(random.nextInt(movieList.getMovies().size()));
        System.out.println("The movie"+movie.getTitle());
        model.addAttribute("movie", movie);
        model.addAttribute("LoggedInUser", user);
        model.addAttribute("movieList", movieList);
        return "movieList";

    }

    @PostMapping("/moveAMovieFromMovieList")
    public String moveAMovieFromMovieList(@RequestParam("movieId") long movieId,
                                          @RequestParam("movieListId") long ListId,
                                          HttpSession session, Model model){
        User user = (User) session.getAttribute("LoggedInUser");
        if(user==null) {
            return "redirect:/loggedin";
        }
        System.out.println("tsesa"+user+"user");
        MovieList movieList=user.getMovieList(ListId);

        if(movieList==null) {
            System.out.println("NULL????"+user+"user");
            return "redirect:/loggedin";
        }

        List<Movie> movies=movieList.getMovies();
        for(Movie m:movies) {
            if(m.getId()==movieId) {
                movies.remove(m);
                movieList.setMovies(movies);
                movieListService.saveMovieList(movieList);
                Watched watched=user.getWatched();
                watched.addMovie(m);
                watchedService.saveWatched(watched);
                break;
            }
        }

        if(movieList.getMovies().isEmpty()) {
            return "redirect:/loggedin";
        }
        model.addAttribute("LoggedInUser", user);
        model.addAttribute("movieList", movieList);
        return "movieList";
    }

    @PostMapping("/deleteMovieList")
    public String deleteMovieList(@RequestParam("movieListId") long movieListId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        MovieList movieList = movieListService.findMovieListById(movieListId);
        if (sessionUser != null && movieList != null) {
            movieListService.deleteMovieList(movieList);
            sessionUser.getMovieLists().removeIf(existingList -> existingList.getId() == movieListId);
            session.setAttribute("LoggedInUser", sessionUser);
        }
        return "redirect:/loggedin";
    }

    @PostMapping("/toggleSettings")
    public String userSettings(HttpSession session) {
        Boolean showSettings = (Boolean) session.getAttribute("DivSettings");
        showSettings = showSettings == null || !showSettings;
        session.setAttribute("DivSettings", showSettings);
        return "redirect:/loggedin";
    }

    @PostMapping("/saveSettings")
    public String saveSettings(HttpSession session, @RequestParam(value = "child", required = false) Boolean child,
                               @RequestParam(value = "username") String username,
                               @RequestParam(value = "password") String password,
                               @RequestParam(value = "email", required = false) String email) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            sessionUser.setChild(child != null && child);
            sessionUser.setUsername(username.trim());
            sessionUser.setPassword(password.trim());
            sessionUser.setEmail(email);
            userService.saveUser(sessionUser);
            session.setAttribute("LoggedInUser", sessionUser);
        }
        return "redirect:/loggedin";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            userService.deleteUser(sessionUser);

            session.invalidate();
        }
        return "redirect:/";
    }

    @PostMapping("/getSuggestedMovie")
    public String getSuggestedMovie(
            HttpSession session,
            Model model,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minVotes,
            @RequestParam(required = false) String certification,
            @RequestParam(required = false) Integer minRuntime,
            @RequestParam(required = false) Integer maxRuntime
    ) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            User loggedInUser = userService.findUserById(sessionUser.getId());

            List<Genre> userGenres = loggedInUser.getGenres();
            Map<String, Object> recommendedMovie;
            List<Map<String, Object>> recommendedMovies;
            if (userGenres != null && !userGenres.isEmpty()) {
                recommendedMovie = tmdbService.getRandomPersonalizedMovie(
                        userGenres,
                        loggedInUser.getChild(),
                        minRating,
                        minVotes,
                        certification,
                        minRuntime,
                        maxRuntime
                );
            } else {
                recommendedMovie = tmdbService.getRandomPopularMovie(loggedInUser.getChild());
            }

            if (recommendedMovie != null) {
                model.addAttribute("recommendedMovie", recommendedMovie);
                model.addAttribute("hasSuggestedMovie", true);
            } else {
                model.addAttribute("errorMessage", "No movies found matching your preferences.");
                model.addAttribute("hasSuggestedMovie", false);
            }

            model.addAttribute("LoggedInUser", loggedInUser);
            model.addAttribute("genres", loggedInUser.getGenres());
            model.addAttribute("movieLists", loggedInUser.getMovieLists());
            Watched watched=loggedInUser.getWatched();
            model.addAttribute("watchedMovies", watched.getMovies());
            model.addAttribute("totalTime",loggedInUser.getTotalTime());


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
    @GetMapping("/GetMoviesByTitle")
    public String getMovieDetailsFromTitles(@RequestParam String query, Model model, HttpSession session) {
        try {
            User loggedInUser = (User) session.getAttribute("LoggedInUser");
            if(!query.isEmpty()&&loggedInUser!=null) {
                query = query.toLowerCase();
                query = query.replace(" ", "+");
                List<Map<String, Object>> recommendedMovies = tmdbService.getMoviesFromTitles(Collections.singletonList(query.trim()));
                model.addAttribute("recommendedMovies", recommendedMovies);
                model.addAttribute("query", query);
                model.addAttribute("LoggedInUser", loggedInUser);
                model.addAttribute("movieLists", loggedInUser.getMovieLists());
                model.addAttribute("watchedMovies", loggedInUser.getWatched().getMovies());
                model.addAttribute("totalTime",loggedInUser.getTotalTime());


                return "loggedInUser";
            }

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to fetch recommendations. Please try again later.");
            e.printStackTrace();
        }
        return "redirect:/loggedin";
    }
    @PostMapping("/addMovieToWatchedUser")
    public String addMovieToWatchedUser(
            @RequestParam("movieTitle") String movieTitle,
            @RequestParam("movieGenreIds") List<String> movieGenreIds,
            @RequestParam("movieOverview") String movieOverview,
            @RequestParam("movieReleaseDate") String movieReleaseDate,
            HttpSession session,Model model) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if(loggedInUser==null) {
            return "redirect:/loggedin";
        }

        Watched watched=loggedInUser.getWatched();
        if(watched==null) {
            watched=new Watched();
            loggedInUser.setWatched(watched);
        }


        List<Genre> genres = Genre.fromString(String.valueOf(movieGenreIds));
        Movie movie = new Movie(movieTitle, genres, movieOverview, movieReleaseDate, 0, 0);

        //if movie is in watched
        for(Movie m:watched.getMovies()) {
            if(m.getTitle().equals(movieTitle)&&m.getReleaseDate().equals(movieReleaseDate)) {
                return "redirect:/GetMoviesByTitle?query=" + movieTitle;

            }
        }
        watched.addMovie(movie);
        watchedService.saveWatched(watched);


        session.setAttribute("LoggedInUser", userService.findUserById(loggedInUser.getId()));
        model.addAttribute("LoggedInUser", loggedInUser);
        model.addAttribute("movieLists", loggedInUser.getMovieLists());
        model.addAttribute("watchedMovies", loggedInUser.getWatched().getMovies());
        model.addAttribute("totalTime",loggedInUser.getTotalTime());
        return "redirect:/GetMoviesByTitle?query=" + movieTitle;
    }


    @PostMapping("/addMovieToListUser")
    public String addMovieToListUser(
            @RequestParam(value = "movieListId",required = false) Long listID ,
            //@RequestParam("movieId") long movieId,
            @RequestParam("movieTitle") String movieTitle,
            @RequestParam("movieGenreIds") List<String> movieGenreIds,
            @RequestParam("movieOverview") String movieOverview,
            @RequestParam("movieReleaseDate") String movieReleaseDate,

            HttpSession session,Model model) {

        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        MovieList movieList = movieListService.findMovieListById(listID);


        List<Genre> genres = Genre.fromString(String.valueOf(movieGenreIds));
        Movie movie = new Movie(movieTitle, genres, movieOverview, movieReleaseDate, 0, 0);


        List<Movie> MoviesInList=movieList.getMovies();
        if(loggedInUser==null|| MoviesInList==null) {
            return "redirect:/loggedin";
        }
        //ef myndinn er þegar í listanum
        for(Movie m:MoviesInList) {
            if(m.getTitle().equals(movieTitle)&&m.getReleaseDate().equals(movieReleaseDate)) {
                return "redirect:/GetMoviesByTitle?query=" + movieTitle;

            }
        }
        movieList.getMovies().add(movie);
        movieListService.saveMovieList(movieList);

        session.setAttribute("LoggedInUser", userService.findUserById(loggedInUser.getId()));
        model.addAttribute("LoggedInUser", loggedInUser);
        model.addAttribute("movieLists", loggedInUser.getMovieLists());
        model.addAttribute("watchedMovies", loggedInUser.getWatched().getMovies());
        model.addAttribute("movieLists",loggedInUser.getMovieLists());
        model.addAttribute("totalTime",loggedInUser.getTotalTime());


        return "redirect:/GetMoviesByTitle?query=" + movieTitle;
    }

    public List<Genre> getGenres(List<String> genreIds) {
        List<Genre> genres = new ArrayList<>();

        if (genreIds != null && !genreIds.isEmpty()) {
            for (String genreId : genreIds) {
                try {
                    Genre genre = Genre.fromTmdbId(Integer.valueOf(genreId));
                    genres.add(genre);
                } catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getMessage());
                }
            }
        }

        return genres;
    }
}
