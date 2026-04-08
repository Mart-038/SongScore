package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * */

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import nl.miwnn.ch19.mart.songscore.repository.RatingRepository;
import nl.miwnn.ch19.mart.songscore.repository.SongRepository;
import nl.miwnn.ch19.mart.songscore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.UUID;

@Controller
public class InitializeController {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(InitializeController.class);

    public InitializeController(
            SongRepository songRepository,
            ArtistRepository artistRepository,
            RatingRepository ratingRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seed() {
        if (artistRepository.count() == 0) {
            artistSeed();
        }
        if (songRepository.count() == 0){
            songSeed();
        }
        if (userRepository.count() == 0) {
            userSeed();
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
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private void userSeed() {
        String password = UUID.randomUUID().toString();

        log.warn("==========================================================");
        log.warn("Password generated for beheerder: {}", password);
        log.warn("==========================================================");

        SongScoreUser admin = new SongScoreUser("admin",
                passwordEncoder.encode(password),
                "ADMIN");
        userRepository.save(admin);
    }
}
