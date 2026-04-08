package nl.miwnn.ch19.mart.songscore.service;

/*
 * @author Mart Stukje
 * Handles business logic regarding ratings from songs placed by users
 * */

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import nl.miwnn.ch19.mart.songscore.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating getRatingById(Long id) {
        Optional<Rating> optionalRating = ratingRepository.findById(id);
        if (optionalRating.isEmpty()) {
            throw new EntityNotFoundException("Deze rating kon niet worden gevonden");
        }
        return optionalRating.get();
    }

    public boolean raterAndSongCombinationAlreadyExists(Song song, SongScoreUser rater) {
        return ratingRepository.existsRatingBySongAndRater(song, rater);
    }

    public void saveRating(Rating rating) {
        ratingRepository.save(rating);
    }

    public void deleteRating(Rating rating) {
        ratingRepository.delete(rating);
    }

}
