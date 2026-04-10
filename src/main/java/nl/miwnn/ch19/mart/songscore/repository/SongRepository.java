package nl.miwnn.ch19.mart.songscore.repository;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findSongByTitleAndArtistsContains(String title, Artist artist);

    List<Song> findByTitleContainingIgnoreCase(String title);

    @Query("""
            SELECT s FROM Song s JOIN s.artists a
            WHERE s.title = :title AND a.id = :artistId""")
    Optional<Song> findSongByTitleAndArtistId(
            @Param("title") String title, @Param("artistId") Long artistId);
}
