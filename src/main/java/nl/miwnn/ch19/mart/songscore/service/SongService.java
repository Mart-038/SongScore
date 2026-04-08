package nl.miwnn.ch19.mart.songscore.service;

/*
 * @author Mart Stukje
 * Handles business logic regarding songs
 * */

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import nl.miwnn.ch19.mart.songscore.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    public SongService(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Song getSongById(Long id) {
        Optional<Song> optionalSong = songRepository.findById(id);
        if (optionalSong.isEmpty()) {
            throw new EntityNotFoundException(String.format("Nummer met id %d kon niet gevonden worden.", id));
        }

        return optionalSong.get();
    }

    @Transactional(readOnly = true)
    public Song getSongByTitleAndArtist(String title, String artistName) {
        Artist firstArtist = artistRepository.findArtistByNameIgnoreCase(artistName)
                .orElseThrow(() -> new IllegalArgumentException("Artist with given name not found"));
        Optional<Song> optionalSong = songRepository.findSongByTitleAndArtistsContains(title, firstArtist);

        if (optionalSong.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Nummer %s van %s bestaat niet.", title, firstArtist.getName()));
        }

        return optionalSong.get();
    }

    @Transactional(readOnly = true)
    public boolean songAndArtistCombinationExists(Song updatedSong) {
        if (updatedSong.getArtists().isEmpty()) {
            return false;
        }

        Artist firstArtist = updatedSong.getArtists().get(0);

        // Bij een nieuw nummer:
        if (updatedSong.getId() == null) {
            return existsByTitleAndArtist(updatedSong, firstArtist);
        }

        Song existingSong = getSongById(updatedSong.getId());

        // Als titel en artiest hetzelfde blijven, mag dit gewoon
        if (sameTitleAndSameArtist(updatedSong, existingSong)) {
            return false;
        }

        // Als titel en/of artiest verandert, checken of combinatie al bestaat
        return existsByTitleAndArtist(updatedSong, firstArtist);
    }

    private boolean sameTitleAndSameArtist(Song updatedSong, Song existingSong) {
        return (updatedSong.getTitle().equals(existingSong.getTitle())) && (existingSong.getArtists().get(0).getName()
                .equals(updatedSong.getArtists().get(0).getName()));
    }

    private boolean existsByTitleAndArtist(Song song, Artist artist) {
        return songRepository.findSongByTitleAndArtistsContains(song.getTitle(), artist).isPresent();
    }

    public void saveSong(Song song) {
        songRepository.save(song);
    }

    public void deleteSong(Song song) {
        songRepository.delete(song);
    }

}
