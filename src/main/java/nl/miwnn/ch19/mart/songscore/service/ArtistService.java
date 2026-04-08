package nl.miwnn.ch19.mart.songscore.service;

/*
 * @author Mart Stukje
 * Handles business logic regarding artists
 * */

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.mart.songscore.dto.ArtistFormDTO;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Image;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import nl.miwnn.ch19.mart.songscore.repository.ImageRepository;
import nl.miwnn.ch19.mart.songscore.service.mapper.ArtistMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final ImageRepository imageRepository;

    public ArtistService(ArtistRepository artistRepository,
                         ArtistMapper artistMapper,
                         ImageRepository imageRepository) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
        this.imageRepository = imageRepository;
    }

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Artist getArtistById(Long id) {
        Optional<Artist> optionalArtist = artistRepository.findById(id);

        if (optionalArtist.isEmpty()) {
            throw new EntityNotFoundException(String.format("Artiest met naam %s niet gevonden.", id));
        }

        return optionalArtist.get();
    }

    public Artist getArtistByName(String artistName) {
        Optional<Artist> optionalArtist = artistRepository.findArtistByNameIgnoreCase(artistName);
        if (optionalArtist.isEmpty()) {
            throw new EntityNotFoundException(String.format("Artiest met naam %s niet gevonden.", artistName));
        }
        return optionalArtist.get();
    }

    public boolean artistNameAlreadyInUse(String name, Long artistId) {
        Optional<Artist> existingArtist = artistRepository.findArtistByNameIgnoreCase(name);
        if (existingArtist.isEmpty()) {
            return false;
        }
        if (artistId == null) {
            return true;
        }
        return !existingArtist.get().getId().equals(artistId);
    }

    public void saveArtist(ArtistFormDTO artistForm, MultipartFile imageFile) {
        Artist artist;

        if (artistForm.getId() != null) {
            artist = artistRepository.findById(artistForm.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Artiest niet gevonden in database"));
        } else {
            artist = new Artist();
        }
        artist = artistMapper.toArtist(artistForm, artist);

        if (!imageFile.isEmpty()) {
            Image image = new Image();
            try {
                image.setData(imageFile.getBytes());
            } catch (IOException ioException) {
                throw new IllegalStateException("Dit bestand kon niet worden opgeslagen", ioException);
            }
            image.setContentType(imageFile.getContentType());
            imageRepository.save(image);
            artist.setImage(image);
        }
        artistRepository.save(artist);
    }


    public void deleteArtistByName(String artistName) {
        Artist artist = getArtistByName(artistName);
        artistRepository.delete(artist);
    }

}
