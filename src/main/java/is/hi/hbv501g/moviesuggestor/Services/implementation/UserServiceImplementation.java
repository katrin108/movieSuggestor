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
    private List<User> usersRepository=new ArrayList<>();//just test
    private int id_counter=0;

   @Autowired
    public UserServiceImplementation() {
        //test
        usersRepository.add(new User("U1","123","test1@email"));
        usersRepository.add(new User("U2","abc","test2@email"));
        usersRepository.add(new User("U3","123","test3@email"));

        // manual id
        for(User user:usersRepository){
            user.setId(id_counter++);
        }
    }

    @Override
    public List<User> findAllUsers() {

        return usersRepository;
    }
    @Override
    public User findUserByUsername(String username) {
        for(User user:usersRepository){
            if(Objects.equals(user.getUsername(), username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        for(User user:usersRepository){
            if(Objects.equals(user.getEmail(), email)){
                return user;
            }
        }
        return null;
    }

    @Override
    public User findUserById(long id) {
        for(User user:usersRepository){
            if(user.getId()==id){
                return user;
            }
        }
        return null;
    }

    @Override
    public User saveUser(User use) {
        use.setId(id_counter++);
        usersRepository.add(use);
        return use;
    }

    @Override
    public void deleteUser(User user) {
        usersRepository.remove(user);

    }
}
