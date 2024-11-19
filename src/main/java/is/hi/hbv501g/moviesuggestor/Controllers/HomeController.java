package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.*;
import is.hi.hbv501g.moviesuggestor.Services.MovieListService;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import is.hi.hbv501g.moviesuggestor.Services.TmdbService;
import is.hi.hbv501g.moviesuggestor.Services.WatchedService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
public class HomeController {

    private final UserService userService;
    private final TmdbService tmdbService;
    private final MovieListService movieListService;
    private final WatchedService watchedService;

    @Autowired
    public HomeController(UserService userService, TmdbService tmdbService, MovieListService movieListService, WatchedService watchedService) {
        this.userService = userService;
        this.tmdbService = tmdbService;
        this.movieListService = movieListService;
        this.watchedService = watchedService;
    }

    @RequestMapping("/")
    public String homePage(Model model, HttpSession session) {
        // All users
        List<User> allUsers = userService.findAllUsers();
        User sessionUser = (User) session.getAttribute("LoggedInUser");

        model.addAttribute("users", allUsers);
        model.addAttribute("genres", Genre.values());

        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            List<Map<String, Object>> personalizedMovies = tmdbService.getMoviesByGenres(sessionUser.getGenres(),sessionUser.getChild());
            
            model.addAttribute("personalizedMovies", personalizedMovies);
        }

        return "home";
    }

    @PostMapping("/")
    public String getNewMoviePost(
            HttpSession session,
            @RequestParam(value = "genres", required = false) List<Genre> selectedGenres,
            @RequestParam(value = "action") String action,
            @RequestParam(value = "child_safe", required = false) Boolean child_safe,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minVotes,
            @RequestParam(required = false) String certification,
            @RequestParam(required = false) Integer minRuntime,
            @RequestParam(required = false) Integer maxRuntime,
            Model model) {

        Map<String, Object> randomMovie = null;
        List<Map<String, Object>> movies = null;

        User sessionUser = (User) session.getAttribute("LoggedInUser");
        Boolean child = child_safe != null ? child_safe : false;
        if (!child && sessionUser != null) {
            child = Boolean.TRUE.equals(sessionUser.getChild());
        }

        if ("Random Movie".equals(action)) {
            randomMovie = tmdbService.getRandomPopularMovie(child);
        }
        else if ("Movie based on selected genres".equals(action)) {
            if (selectedGenres != null && !selectedGenres.isEmpty()) {
                randomMovie = tmdbService.getRandomPersonalizedMovie(
                        selectedGenres,
                        child,
                        minRating,
                        minVotes,
                        certification,
                        minRuntime,
                        maxRuntime
                );
                movies = null;
            }
            else {
                randomMovie = tmdbService.getRandomPopularMovie(child);
                movies = null;
            }
        }
        else if ("Movies based on selected genres".equals(action)) {
            if (selectedGenres != null && !selectedGenres.isEmpty()) {
                movies = tmdbService.getPersonalizedMovies(
                        selectedGenres,
                        child,
                        minRating,
                        minVotes,
                        certification,
                        minRuntime,
                        maxRuntime
                );
            }
        }
        else if ("Movie based on saved genres".equals(action)) {
            if (sessionUser != null && sessionUser.getGenres() != null && !sessionUser.getGenres().isEmpty()) {
                randomMovie = tmdbService.getRandomPersonalizedMovie(
                        sessionUser.getGenres(),
                        child,
                        minRating,
                        minVotes,
                        certification,
                        minRuntime,
                        maxRuntime
                );
            } else {
                randomMovie = tmdbService.getRandomPopularMovie(child);
                movies = null;
            }
        }
        else if ("Movies based on saved genres".equals(action)) {
            if (sessionUser != null && sessionUser.getGenres() != null && !sessionUser.getGenres().isEmpty()) {
                movies = tmdbService.getPersonalizedMovies(
                        sessionUser.getGenres(),
                        child,
                        minRating,
                        minVotes,
                        certification,
                        minRuntime,
                        maxRuntime
                );
                if (movies != null) {
                    for(Map<String, Object> m:movies){

                    }
                }
                randomMovie = null;
            }
        }
        else {
            randomMovie = tmdbService.getRandomPopularMovie(child);
            movies = null;
        }

        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
        }
        model.addAttribute("child_safe", child);

        if (randomMovie != null) {
            model.addAttribute("tmdbMovie", randomMovie);
            model.addAttribute("movieGenre", tmdbService.getGenre(randomMovie));
        } else if (movies != null){
            model.addAttribute("recommendedMovies", movies);
        }
        else {
            model.addAttribute("errorMessage", "No movies found matching your criteria.");
        }

        if(sessionUser!=null) {
            model.addAttribute("movieLists", sessionUser.getMovieLists());
        }


        model.addAttribute("genres", Genre.values());
        model.addAttribute("selectedGenres", selectedGenres);

        return "home";
    }



    @PostMapping("/addMovieToWatched")
    public String addMovieToWatched(
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
        model.addAttribute("watchedMovies", watched.getMovies());
        model.addAttribute("watched",watched);

        return "home";
    }


    @PostMapping("/addMovieToList")
    public String addMovieToList(
            @RequestParam("movieListId") Long listID,
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
            return "home";
        }
        //ef myndinn er þegar í listanum
        for(Movie m:MoviesInList) {
            if(m.getTitle().equals(movieTitle)&&m.getReleaseDate().equals(movieReleaseDate)) {
                return "home";

            }
        }
        movieList.getMovies().add(movie);
        movieListService.saveMovieList(movieList);

        session.setAttribute("LoggedInUser", userService.findUserById(loggedInUser.getId()));
        model.addAttribute("LoggedInUser", loggedInUser);
        model.addAttribute("movieLists", loggedInUser.getMovieLists());
        model.addAttribute("watchedMovies", loggedInUser.getWatched().getMovies());
        model.addAttribute("totalTime",loggedInUser.getTotalTime());


        return "home";
    }




}
