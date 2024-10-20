package is.hi.hbv501g.moviesuggestor.Services;

//import com.zaxxer.hikari.util.ClockSource;
import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Favorites;

import java.util.List;

public interface FavoritesService  {
    List<Favorites> findAllFavorites();
    //Favorites findFavoritesById(long id);

    //Favorites saveFavorites(Favorites favorites);
    //void deleteFavorites(Favorites favorites);
}
