package is.hi.hbv501g.moviesuggestor.Persistence.Repositories;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchedRepository extends JpaRepository<Watched, Integer> {
    Watched save(Watched watched);
    void delete(Watched watched);
    List<Watched> findAll();
    Watched findById(long id);
}
