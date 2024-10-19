package is.hi.hbv501g.moviesuggestor.Persistence.Repositories;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Favorites;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.User;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Movie;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    Favorites save(Favorites favorites);
    void delete(Favorites favorites);

    List<Favorites> findAllFavorites();

    Favorites findFavoritesById(long id);
    Favorites findFavoritesByUser(User user);
}
