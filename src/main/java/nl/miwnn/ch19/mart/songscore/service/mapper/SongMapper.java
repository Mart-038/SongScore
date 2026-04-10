package nl.miwnn.ch19.mart.songscore.service.mapper;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.dto.SongFormDTO;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Song;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SongMapper {

    public Song toSong (SongFormDTO dto, Song song, List<Artist> artists) {
        song.setTitle(dto.getTitle());
        song.setGenre(dto.getGenre());
        song.setYear(dto.getYear());
        song.setArtists(artists);

        return song;
    }

    public SongFormDTO toDto (Song song) {
        SongFormDTO dto = new SongFormDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setGenre(song.getGenre());
        dto.setYear(song.getYear());
        dto.setArtistIds(song.getArtists().stream()
                .map(Artist::getId).toList());
        return dto;
    }
}
