package nl.miwnn.ch19.mart.songscore.repository;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsRatingBySongAndRater(Song song, SongScoreUser rater);

    @Query("SELECT AVG(r.rateScore) FROM Rating r JOIN r.song s JOIN s.artists a WHERE a.id = :artistId")
    Double findAverageRatingByArtistId(@Param("artistId") Long artistId);
}
