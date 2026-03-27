package nl.miwnn.ch19.mart.songscore.model;

/*
 * @author Mart Stukje
 * A song played by one or more artists that users can rate
 * */

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "De titel mag niet leeg zijn")
    @Size(max = 200, message = "De titel mag niet meer dan {max} tekens bevatten")
    private String title;

    @ManyToMany
    private List<Artist> artists = new ArrayList<>();

    @NotBlank(message = "Het genre mag niet leeg zijn")
    @Size(max = 200, message = "Het genre mag niet meer dan {max} tekens bevatten")
    private String genre;

    @NotNull(message = "Het jaar mag niet leeg zijn")
    @Min(value = 1000, message = "Het jaar mag niet lager dan {value} zijn")
    @Max(value = 2030, message = "Het jaar mag niet hoger dan {value} zijn")
    private Integer year;

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    List<Rating> ratings = new ArrayList<>();

    public Song(String title, List<Artist> artists, String genre, int year) {
        this.title = title;
        this.artists = artists;
        this.genre = genre;
        this.year = year;
    }

    public Song() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
