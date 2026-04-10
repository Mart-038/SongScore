package nl.miwnn.ch19.mart.songscore.service;

/*
 * @author Mart Stukje
 * Handles business logic regarding songs
 * */

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.mart.songscore.dto.SongFormDTO;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Image;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import nl.miwnn.ch19.mart.songscore.repository.ImageRepository;
import nl.miwnn.ch19.mart.songscore.repository.SongRepository;
import nl.miwnn.ch19.mart.songscore.service.mapper.SongMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final ImageService imageService;
    private final SongMapper songMapper;

    public SongService(SongRepository songRepository, ArtistRepository artistRepository,
                       ImageService imageService, SongMapper songMapper) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.imageService = imageService;
        this.songMapper = songMapper;
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public List<Song> searchSongsByTitleContaining(String query) {
        return songRepository.findByTitleContainingIgnoreCase(query);
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
    public boolean songAndArtistCombinationExists(SongFormDTO updatedSongDto) {
        if (updatedSongDto.getArtistIds().isEmpty()) {
            return false;
        }

        String updatedSongTitle = updatedSongDto.getTitle();
        Long firstArtistId = updatedSongDto.getArtistIds().get(0);

        // Bij een nieuw nummer:
        if (updatedSongDto.getId() == null) {
            return existsByTitleAndArtist(updatedSongTitle, firstArtistId);
        }

        Song existingSong = getSongById(updatedSongDto.getId());

        // Als titel en artiest hetzelfde blijven, mag dit gewoon
        if (sameTitleAndSameArtist(updatedSongDto, existingSong)) {
            return false;
        }

        // Als titel en/of artiest verandert, checken of combinatie al bestaat
        return existsByTitleAndArtist(updatedSongTitle, firstArtistId);
    }

    private boolean sameTitleAndSameArtist(SongFormDTO updatedSongDto, Song existingSong) {
        return (updatedSongDto.getTitle().equals(existingSong.getTitle())) && (existingSong.getArtists().get(0).getId()
                .equals(updatedSongDto.getArtistIds().get(0)));
    }

    private boolean existsByTitleAndArtist(String title, Long artistId) {
        return songRepository.findSongByTitleAndArtistId(title, artistId).isPresent();
    }

    public void saveSong(SongFormDTO dto, MultipartFile albumCoverFile) {
        Song song;

        if (dto.getId() != null) {
            song = songRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Song not found in database"));
        } else {
            song = new Song();
        }

        List<Artist> artists = artistRepository.findAllById(dto.getArtistIds());

        song = songMapper.toSong(dto, song, artists);

        if (!albumCoverFile.isEmpty()) {
            song.setAlbumCover(imageService.saveImage(albumCoverFile, "albumCover"));
        } else if (dto.getExistingImageId() != null) {
            Image existing = imageService.getImageById(dto.getExistingImageId());
            song.setAlbumCover(existing);
        }
        songRepository.save(song);
    }

    public void deleteSong(Song song) {
        songRepository.delete(song);
    }

}
