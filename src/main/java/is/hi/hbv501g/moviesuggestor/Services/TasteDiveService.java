package is.hi.hbv501g.moviesuggestor.Services;

import is.hi.hbv501g.moviesuggestor.Models.TasteDiveResponse;

import java.util.List;

public interface TasteDiveService {
    List<String> getRecommendations(String query);

    List<TasteDiveResponse.Info> getMovieInfo(String query);
}
