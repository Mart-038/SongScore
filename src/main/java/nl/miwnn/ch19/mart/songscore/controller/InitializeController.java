package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * */

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import nl.miwnn.ch19.mart.songscore.repository.RatingRepository;
import nl.miwnn.ch19.mart.songscore.repository.SongRepository;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Controller
public class InitializeController {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final RatingRepository ratingRepository;


    public InitializeController(
            SongRepository songRepository, ArtistRepository artistRepository, RatingRepository ratingRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.ratingRepository = ratingRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seed() {
        if (artistRepository.count() == 0) {
            artistSeed();
        }
        if (songRepository.count() == 0){
            songSeed();
        }
    }

    private void artistSeed() {
        try {
            ClassPathResource resource = new ClassPathResource("/seedData/artists.csv");
            Reader reader = new InputStreamReader(resource.getInputStream());

            CsvToBean<Artist> csvToBean = new CsvToBeanBuilder<Artist>(reader)
                    .withType(Artist.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            artistRepository.saveAll(csvToBean.parse());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private void songSeed() {
        try {
            ClassPathResource resource = new ClassPathResource("/seedData/songs.csv");
            Reader reader = new InputStreamReader(resource.getInputStream());

            CsvToBean<Song> csvToBean = new CsvToBeanBuilder<Song>(reader)
                    .withType(Song.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Song> songs = csvToBean.parse();
            List<Artist> artists = artistRepository.findAll();

            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                song.getArtists().add(artists.get(i % artists.size()));
                songRepository.save(song);

                ratingRepository.save(new Rating(1, song));
                ratingRepository.save(new Rating(3, song));
                ratingRepository.save(new Rating(5, song));
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
