package nl.miwnn.ch19.mart.songscore.dto;

/*
 * @author Mart Stukje
 * Supports the CSV with ratings as seed data for SongScore
 * */


import com.opencsv.bean.CsvBindByName;

public class RatingCsvDTO {

    @CsvBindByName(column = "username")
    String username;

    @CsvBindByName(column = "songTitle")
    String songTitle;

    @CsvBindByName(column = "artistName")
    String artistName;

    @CsvBindByName(column = "rateScore")
    int rateScore;

    @CsvBindByName(column = "description")
    String description;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getRateScore() {
        return rateScore;
    }

    public void setRateScore(int rateScore) {
        this.rateScore = rateScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
