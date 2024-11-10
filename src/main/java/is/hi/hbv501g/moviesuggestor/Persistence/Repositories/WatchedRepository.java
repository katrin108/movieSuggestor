package is.hi.hbv501g.moviesuggestor.Persistence.Repositories;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Watched;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchedRepository extends JpaRepository<Watched, Integer> {
    Watched findByUserId(Long userId);
    Watched save(Watched watched);
}
