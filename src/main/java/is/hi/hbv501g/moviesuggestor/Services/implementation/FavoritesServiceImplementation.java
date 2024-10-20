package is.hi.hbv501g.moviesuggestor.Services.implementation;

import is.hi.hbv501g.moviesuggestor.Persistence.Entities.Favorites;
import is.hi.hbv501g.moviesuggestor.Persistence.Repositories.FavoritesRepository;
import is.hi.hbv501g.moviesuggestor.Services.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FavoritesServiceImplementation implements FavoritesService {

    private FavoritesRepository favoritesRepository;

    @Autowired
    public FavoritesServiceImplementation(FavoritesRepository favoritesRepository) { this.favoritesRepository = favoritesRepository; }

    @Override
    public List<Favorites> findAllFavorites() { return favoritesRepository.findAll(); }


}
