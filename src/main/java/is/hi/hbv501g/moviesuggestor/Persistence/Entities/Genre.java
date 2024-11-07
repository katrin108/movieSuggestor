package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Genre {
    ACTION(28),
    COMEDY(35),
    DRAMA(18),
    HORROR(27),
    ROMANCE(10749),
    ADVENTURE(12),
    THRILLER(53),
    SCIFI(878),
    DOCUMENTARY(99);

    private final int tmdbId;

    Genre(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public int getTmdbId() {
        return tmdbId;
    }
}
