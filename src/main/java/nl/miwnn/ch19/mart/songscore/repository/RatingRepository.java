package nl.miwnn.ch19.mart.songscore.repository;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsRatingBySongAndRater(Song song, SongScoreUser rater);
}
