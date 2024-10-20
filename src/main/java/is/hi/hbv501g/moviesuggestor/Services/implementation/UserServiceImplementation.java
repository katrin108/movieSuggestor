package is.hi.hbv501g.moviesuggestor.Services.implementation;


import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Genre;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.MovieListRepository;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.UserRepository;
import is.hi.hbv501g.moviesuggestor.Services.MovieListService;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;
    private MovieListRepository movieListRepository;
    private MovieListService movieListService;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository, MovieListRepository movieListRepository, MovieListService movieListService) {

        this.userRepository = userRepository;
        this.movieListRepository = movieListRepository;
        this.movieListService = movieListService;
    }


    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }



    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByUsername(email);
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }


    @Override
    public User login(User user){
        User doseExist =findUserByUsername(user.getUsername());
        if(doseExist != null){
            if(doseExist.getPassword().equals(user.getPassword())){
                return doseExist;
            }
        }
        return null;
    }
    @Override
    public void setGenres(User user, List<Genre> genres){
        user.setGenres(genres);
        userRepository.save(user);
    }

    @Override
    public MovieList findMovieListById(long id) {
        return movieListRepository.findMovieListById(id);
    }


    @Override
    public MovieList findMoveListByTitle(String name) {
        return movieListRepository.findMoveListByName(name);
    }

    @Override
    public MovieList saveMovieList(MovieList movieList) {
        return movieListService.saveMovieList(movieList);
    }

    @Override
    public void deleteMovieList(MovieList movieList) {
        movieListService.deleteMovieList(movieList);
    }




}