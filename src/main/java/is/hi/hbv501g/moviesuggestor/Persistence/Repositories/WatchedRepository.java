package is.hi.hbv501g.moviesuggestor.Persistence.Repositories;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchedRepository extends JpaRepository<Watched, Long> {
    Watched save(Watched watched);
    void delete(Watched watched);

    List<Watched> findAll();
    Watched findById(long id);
}
