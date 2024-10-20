package is.hi.hbv501g.moviesuggestor.Persistence.Repositories;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.MovieList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieListRepository extends JpaRepository<MovieList, Long> {
    MovieList save(MovieList movieList);
    void delete(MovieList id);

    List<MovieList> findAll();
    MovieList findMovieListById(long id);
    MovieList findMoveListByName (String name);

    MovieList getMovieListsById(long id);
}
