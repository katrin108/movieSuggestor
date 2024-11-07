package is.hi.hbv501g.moviesuggestor.Persistence.Entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Genre {

    ACTION(28),
    ADVENTURE(12),
    ANIMATION(16),
    COMEDY(35),
    CRIME(80),
    DOCUMENTARY(99),
    DRAMA(18),
    FAMILY(10751),
    FANTASY(14),
    HISTORY(36),
    HORROR(27),
    MUSIC(10402),
    MYSTERY(9648),
    ROMANCE(10749),
    SCIENCE_FICTION(878),
    TV_MOVIE(10770),
    THRILLER(53),
    WAR(10752),
    WESTERN(37);


    private final int tmdbId;

    Genre(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public static Genre fromTmdbId(int tmdbId) {
        for (Genre genre : Genre.values()) {
            if (genre.getTmdbId() == tmdbId) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Invalid tmdbId: " + tmdbId);
    }
}
