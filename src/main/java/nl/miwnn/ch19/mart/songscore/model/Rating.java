package nl.miwnn.ch19.mart.songscore.model;

/*
 * @author Mart Stukje
 * A rating of a song that a user can make
 * */

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"rater_id", "song_id"})
)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Geef een waardering op")
    @Min(value = 1, message = "Waardering moet ten minste 1 zijn")
    @Max(value = 5, message = "Waardering mag hoogstens 5 zijn")
    private Integer rateScore;

    private String description;

    @ManyToOne
    private Song song;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SongScoreUser rater;

    private LocalDateTime placedAt;

    public Rating(int rateScore, Song song) {
        this.rateScore = rateScore;
        this.song = song;
    }

    public Rating() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRateScore() {
        return rateScore;
    }

    public void setRateScore(Integer rateScore) {
        this.rateScore = rateScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public SongScoreUser getRater() {
        return rater;
    }

    public void setRater(SongScoreUser rater) {
        this.rater = rater;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }
}
