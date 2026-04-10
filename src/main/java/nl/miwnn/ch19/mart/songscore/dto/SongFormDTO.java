package nl.miwnn.ch19.mart.songscore.dto;

/*
 * @author Mart Stukje
 * Supports new/edit song form
 * */

import jakarta.validation.constraints.*;

import java.util.List;

public class SongFormDTO {

    private Long id;

    @NotBlank(message = "De titel mag niet leeg zijn")
    @Size(max = 200, message = "De titel mag niet meer dan {max} tekens bevatten")
    private String title;

    @NotEmpty(message = "Er moet minstens één artiest geselecteerd worden")
    private List<Long> artistIds;

    @NotBlank(message = "Het genre mag niet leeg zijn")
    @Size(max = 200, message = "Het genre mag niet meer dan {max} tekens bevatten")
    private String genre;

    @NotNull(message = "Het jaar mag niet leeg zijn")
    @Min(value = 1000, message = "Het jaar mag niet lager dan {value} zijn")
    @Max(value = 2030, message = "Het jaar mag niet hoger dan {value} zijn")
    private Integer year;

    private Long existingImageId;

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

    public List<Long> getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(List<Long> artistIds) {
        this.artistIds = artistIds;
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

    public Long getExistingImageId() {
        return existingImageId;
    }

    public void setExistingImageId(Long existingImageId) {
        this.existingImageId = existingImageId;
    }
}
