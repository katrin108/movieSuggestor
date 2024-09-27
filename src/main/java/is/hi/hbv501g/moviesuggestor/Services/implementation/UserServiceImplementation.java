package is.hi.hbv501g.moviesuggestor.Services.implementation;


import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.UserRepository;
import is.hi.hbv501g.moviesuggestor.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImplementation implements UserService {
   // private UserRepository userRepository;
    private List<User> usersReposotory=new ArrayList<>();//just test
    private int id_counter=0;

   @Autowired
    public UserServiceImplementation() {
        //test
        usersReposotory.add(new User("U1","123","test1@email"));
        usersReposotory.add(new User("U2","abc","test2@email"));
        usersReposotory.add(new User("U3","123","test3@email"));

        // manual id
        for(User user:usersReposotory){
            user.setId(id_counter++);
        }
    }

    @Override
    public List<User> findAllUsers() {

        return usersReposotory;
    }
    @Override
    public User findUserByUsername(String username) {
        for(User user:usersReposotory){
            if(Objects.equals(user.getUsername(), username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        for(User user:usersReposotory){
            if(Objects.equals(user.getEmail(), email)){
                return user;
            }
        }
        return null;
    }

    @Override
    public User findUserById(long id) {
        for(User user:usersReposotory){
            if(user.getId()==id){
                return user;
            }
        }
        return null;
    }

    @Override
    public User saveUser(User use) {
        use.setId(id_counter++);
        usersReposotory.add(use);
        return use;
    }

    @Override
    public void deleteUser(User user) {
        usersReposotory.remove(user);

    }
}
