package nl.miwnn.ch19.mart.songscore.service.mapper;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.dto.ArtistFormDTO;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

    public ArtistFormDTO toFormDto(Artist artist) {
        ArtistFormDTO dto = new ArtistFormDTO();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setActiveSince(artist.getActiveSince());
        dto.setActiveUntil(artist.getActiveUntil());

        if (artist.getImage() != null) {
            dto.setExistingImageId(artist.getImage().getId());
        }

        return dto;
    }

    public Artist toArtist(ArtistFormDTO dto, Artist artist) {
        artist.setName(dto.getName());
        artist.setActiveSince(dto.getActiveSince());
        artist.setActiveUntil(dto.getActiveUntil());

        return artist;
    }
}
