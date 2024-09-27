package is.hi.hbv501g.moviesuggestor.Persistence.Repositories;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User save(User user);
    void delete(User user);

    List<User> findAll();


    User findAllById(long id);

}
