package nl.miwnn.ch19.mart.songscore.model;

/*
 * @author Mart Stukje
 * A rating of a song that a user can make
 * */

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Waardering moet ten minste 1 zijn")
    @Max(value = 5, message = "Waardering mag hoogstens 5 zijn")
    private int rateScore;

    @ManyToOne
    private Song song;

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

    public int getRateScore() {
        return rateScore;
    }

    public void setRateScore(int rateScore) {
        this.rateScore = rateScore;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
