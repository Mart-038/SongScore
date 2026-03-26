package nl.miwnn.ch19.mart.songscore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
/*
 * @author Mart Stukje
 * */

class SongTest {

    @ParameterizedTest(name = "Song with year {0}")
    @ValueSource(ints = {1450, 1784, 1974, 2014, 2026})
    @DisplayName("Using song constructor results in correct year for song")
    void usingSongConstructorResultsInCorrectYearForSong(int year) {
        ArrayList<Artist> artists = new ArrayList<>();
        artists.add(new Artist());
        Song song = new Song("Test", artists, "Test", year);
        assertEquals(year, song.getYear());
    }

}