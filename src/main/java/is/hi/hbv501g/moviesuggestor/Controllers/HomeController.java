package is.hi.hbv501g.moviesuggestor.Controllers;

import is.hi.hbv501g.moviesuggestor.Models.TasteDiveResponse;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.*;
import is.hi.hbv501g.moviesuggestor.Services.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final UserService userService;
    private final TmdbService tmdbService;
    private final MovieListService movieListService;
    private final WatchedService watchedService;
    private final TasteDiveService tasteDiveService;

    @Autowired
    public HomeController(UserService userService, TmdbService tmdbService, MovieListService movieListService,
                          WatchedService watchedService, TasteDiveService tasteDiveService) {
        this.userService = userService;
        this.tmdbService = tmdbService;
        this.movieListService = movieListService;
        this.watchedService = watchedService;
        this.tasteDiveService = tasteDiveService;
    }

    @RequestMapping("/")
    public String homePage(Model model, HttpSession session) {
        log.debug("[DEBUG] Entering homePage() method");

        List<User> allUsers = userService.findAllUsers();
        log.debug("[DEBUG] Retrieved all users: {}", allUsers);
        model.addAttribute("users", allUsers);


        User sessionUser = (User) session.getAttribute("LoggedInUser");
        log.debug("[DEBUG] LoggedInUser from session: {}", sessionUser != null ? sessionUser.getUsername() : "No user logged in");

        model.addAttribute("LoggedInUser", sessionUser);
        if(sessionUser != null) {
            model.addAttribute("movieLists", sessionUser.getMovieLists());
            model.addAttribute("watchedMovies", sessionUser.getWatched().getMovies());
            model.addAttribute("movieLists",sessionUser.getMovieLists());
        }
        model.addAttribute("genres", Genre.values());
        model.addAttribute("searchPerformed", false);

        if (sessionUser != null) {
            List<Map<String, Object>> personalizedMovies = tmdbService.getMoviesByGenres(sessionUser.getGenres(), sessionUser.getChild());
            log.debug("[DEBUG] Personalized movies for user {}: {}", sessionUser.getUsername(), personalizedMovies);
            model.addAttribute("personalizedMovies", personalizedMovies);
        }

        log.debug("[DEBUG] Returning view: home");
        return "home";
    }

    @GetMapping("/tastedive")
    public String getTasteDiveRecommendations(@RequestParam("query") String query, Model model, HttpSession session) {
        log.debug("[DEBUG] Entering getTasteDiveRecommendations() with query: {}", query);

        try {
            List<String> recommendations = tasteDiveService.getRecommendations(query);
            if (recommendations == null || recommendations.isEmpty()) {
                log.warn("[WARNING] No recommendations found for query: {}", query);
                model.addAttribute("tasteDiveError", "TasteDive failed to find a result. Please simplify your request.");
                recommendations = Collections.emptyList();
            } else if (recommendations.size() > 10) {
                recommendations = recommendations.subList(0, 10);
            }

            log.debug("[DEBUG] TasteDive recommendations: {}", recommendations);
            model.addAttribute("recommendations", recommendations);
        } catch (Exception e) {
            log.error("[ERROR] Error fetching recommendations from TasteDive: {}", e.getMessage());
            model.addAttribute("tasteDiveError", "An error occurred while fetching recommendations. Please try again later.");
            model.addAttribute("recommendations", Collections.emptyList());
        }

        User sessionUser = (User) session.getAttribute("LoggedInUser");
        log.debug("[DEBUG] LoggedInUser from session: {}", sessionUser != null ? sessionUser.getUsername() : "No user logged in");
        if(sessionUser != null) {
            model.addAttribute("movieLists", sessionUser.getMovieLists());
            model.addAttribute("watchedMovies", sessionUser.getWatched().getMovies());
            model.addAttribute("movieLists",sessionUser.getMovieLists());
        }


        model.addAttribute("LoggedInUser", sessionUser);
        model.addAttribute("genres", Genre.values());
        model.addAttribute("query", query);
        model.addAttribute("searchPerformed", true);

        log.debug("[DEBUG] Returning view: home");
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

        log.debug("[DEBUG] Entering getNewMoviePost() with action: {}", action);

        Map<String, Object> randomMovie = null;
        List<Map<String, Object>> movies = null;
        boolean searchPerformed = false;

        User sessionUser = (User) session.getAttribute("LoggedInUser");
        log.debug("[DEBUG] LoggedInUser from session: {}", sessionUser != null ? sessionUser.getUsername() : "No user logged in");

        Boolean child = child_safe != null ? child_safe : (sessionUser != null && sessionUser.getChild());
        log.debug("[DEBUG] Child-safe mode: {}", child);

        try {
            if ("Random Movie".equals(action)) {
                randomMovie = tmdbService.getRandomPopularMovie(child);
                log.debug("[DEBUG] Random movie: {}", randomMovie);
                searchPerformed = true;
            } else if ("Movie based on selected genres".equals(action)) {
                if (selectedGenres != null && !selectedGenres.isEmpty()) {
                    randomMovie = tmdbService.getRandomPersonalizedMovie(selectedGenres, child, minRating, minVotes, certification, minRuntime, maxRuntime);
                    log.debug("[DEBUG] Random movie based on selected genres: {}", randomMovie);
                    searchPerformed = true;
                } else {
                    randomMovie = tmdbService.getRandomPopularMovie(child);
                    log.debug("[DEBUG] Fallback to random popular movie: {}", randomMovie);
                }
            } else if ("Movies based on selected genres".equals(action)) {
                if (selectedGenres != null && !selectedGenres.isEmpty()) {
                    movies = tmdbService.getPersonalizedMovies(selectedGenres, child, minRating, minVotes, certification, minRuntime, maxRuntime);
                    log.debug("[DEBUG] Movies based on selected genres: {}", movies);
                    searchPerformed = true;
                }
            } else if ("Movie based on saved genres".equals(action) || "Movies based on saved genres".equals(action)) {
                if (sessionUser != null && sessionUser.getGenres() != null && !sessionUser.getGenres().isEmpty()) {
                    if ("Movie based on saved genres".equals(action)) {
                        randomMovie = tmdbService.getRandomPersonalizedMovie(sessionUser.getGenres(), child, minRating, minVotes, certification, minRuntime, maxRuntime);
                        log.debug("[DEBUG] Random movie based on saved genres: {}", randomMovie);
                    } else {
                        movies = tmdbService.getPersonalizedMovies(sessionUser.getGenres(), child, minRating, minVotes, certification, minRuntime, maxRuntime);
                        log.debug("[DEBUG] Movies based on saved genres: {}", movies);
                    }
                    searchPerformed = true;
                }
            } else {
                randomMovie = tmdbService.getRandomPopularMovie(child);
                log.debug("[DEBUG] Fallback to random popular movie: {}", randomMovie);
            }
        } catch (Exception e) {
            log.error("[ERROR] Error during movie search: {}", e.getMessage());
            model.addAttribute("errorMessage", "An error occurred while searching for movies. Please try again.");
        }

        model.addAttribute("LoggedInUser", sessionUser);
        model.addAttribute("child_safe", child);
        if(sessionUser!=null){
            model.addAttribute("movieLists", sessionUser.getMovieLists());
            model.addAttribute("watchedMovies", sessionUser.getWatched().getMovies());
            model.addAttribute("movieLists",sessionUser.getMovieLists());
        }


        if (randomMovie != null) {
            model.addAttribute("tmdbMovie", randomMovie);
            model.addAttribute("movieGenre", tmdbService.getGenre(randomMovie));
        } else if (movies != null) {
            model.addAttribute("recommendedMovies", movies);
        } else {
            model.addAttribute("errorMessage", "No movies found matching your criteria.");
            log.debug("[DEBUG] No movies found matching criteria");
        }

        model.addAttribute("searchPerformed", searchPerformed);
        model.addAttribute("genres", Genre.values());
        model.addAttribute("selectedGenres", selectedGenres);

        log.debug("[DEBUG] Returning view: home");
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
            return "home";
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
                return "home";

            }
        }
        watched.addMovie(movie);
        watchedService.saveWatched(watched);

        session.setAttribute("LoggedInUser", userService.findUserById(loggedInUser.getId()));

        model.addAttribute("LoggedInUser", loggedInUser);
        model.addAttribute("watchedMovies", watched.getMovies());
        model.addAttribute("movieLists",loggedInUser.getMovieLists());
        model.addAttribute("watched",watched);
        model.addAttribute("genres", Genre.values());
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

        if(loggedInUser==null) {
            return "home";
        }
        if(listID==null){
            return "home";
        }

        MovieList movieList = movieListService.findMovieListById(listID);
        List<Genre> genres = Genre.fromString(String.valueOf(movieGenreIds));
        Movie movie = new Movie(movieTitle, genres, movieOverview, movieReleaseDate, 0, 0);


        //ef myndinn er þegar í listanum
        for(Movie m:movieList.getMovies()) {
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
        model.addAttribute("movieLists",loggedInUser.getMovieLists());
        model.addAttribute("totalTime",loggedInUser.getTotalTime());
        model.addAttribute("genres", Genre.values());

        return "home";
    }




}
