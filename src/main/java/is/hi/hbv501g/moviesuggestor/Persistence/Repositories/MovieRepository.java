package is.hi.hbv501g.moviesuggestor.Persistence.Repositories;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Long> {
    Movie save(Movie movie);
    void delete(Movie movie);

    List<Movie> findAll();
    List<Movie> findByTitle(String title);

    Movie findAllById(long id);

}
