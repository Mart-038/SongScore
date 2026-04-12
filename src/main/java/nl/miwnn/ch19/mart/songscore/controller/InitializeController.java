package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * */

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import nl.miwnn.ch19.mart.songscore.dto.RatingCsvDTO;
import nl.miwnn.ch19.mart.songscore.model.*;
import nl.miwnn.ch19.mart.songscore.repository.*;
import nl.miwnn.ch19.mart.songscore.service.SongScoreUserService;
import nl.miwnn.ch19.mart.songscore.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class InitializeController {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;
    private final SongService songService;
    private final SongScoreUserService songScoreUserService;

    private final Logger log = LoggerFactory.getLogger(InitializeController.class);

    @Value("${songScore.seed.mart.password}")
    private String martPassword;

    @Value("${songScore.seed.elias.password}")
    private String eliasPassword;

    @Value("${songScore.seed.yasmine.password}")
    private String yasminePassword;

    @Value("${songScore.seed.michael.password}")
    private String michaelPassword;

    public InitializeController(
            SongRepository songRepository,
            ArtistRepository artistRepository,
            RatingRepository ratingRepository,
            UserRepository userRepository,
            ImageRepository imageRepository,
            PasswordEncoder passwordEncoder,
            SongService songService,
            SongScoreUserService songScoreUserService) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.passwordEncoder = passwordEncoder;
        this.songService = songService;
        this.songScoreUserService = songScoreUserService;
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
        if (ratingRepository.count() == 0) {
            ratingSeed();
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

            Map<String, String> artistImages = Map.ofEntries(
                    Map.entry("The Beatles", "the_beatles.jpg"),
                    Map.entry("Pink Floyd", "pink_floyd.jpg"),
                    Map.entry("David Bowie", "david_bowie.jpg"),
                    Map.entry("Queen", "queen.jpg"),
                    Map.entry("Michael Jackson", "michael_jackson.jpeg"),
                    Map.entry("Radiohead", "radiohead.avif"),
                    Map.entry("Coldplay", "coldplay.jpg"),
                    Map.entry("Adele", "adele.avif"),
                    Map.entry("Ed Sheeran", "ed_sheeran.avif"),
                    Map.entry("Neil Young", "neil_young.jpg"),
                    Map.entry("James Taylor", "james_taylor.webp"),
                    Map.entry("Billy Joel", "billy_joel.jpg"),
                    Map.entry("Golden Earring", "golden_earring.webp"),
                    Map.entry("Madonna", "madonna.webp"),
                    Map.entry("Crosby Stills Nash & Young", "crosby_stills_nash_young.jpg"),
                    Map.entry("Elton John", "elton_john.webp"),
                    Map.entry("Paul McCartney", "paul_mccartney.jpg")
            );

            List<Artist> artists = csvToBean.parse();

            for (Artist artist : artists) {
                String filename = artistImages.get(artist.getName());
                if (filename != null) {
                    artist.setImage(loadImage("/images/seedData/" + filename, "artistImage"));
                }
                artistRepository.save(artist);
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private void songSeed() {
        Reader reader;
        try {
            ClassPathResource resource = new ClassPathResource("/seedData/songs.csv");
            reader = new InputStreamReader(resource.getInputStream());

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        CsvToBean<Song> csvToBean = new CsvToBeanBuilder<Song>(reader)
                .withType(Song.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

            Map<String, String> songImages = Map.ofEntries(
                    Map.entry("Hey Jude", "Heyjude1.png"),
                    Map.entry("Comfortably Numb", "The_Wall_Cover.svg"),
                    Map.entry("Heroes", "heroes.png"),
                    Map.entry("Bohemian Rhapsody", "Queen_A_Night_At_The_Opera.png"),
                    Map.entry("Billie Jean", "Michael_Jackson_-_Thriller.png"),
                    Map.entry("Thriller", "Michael_Jackson_-_Thriller.png"),
                    Map.entry("Beat It", "Michael_Jackson_-_Thriller.png"),
                    Map.entry("Ben", "BenMichaelJackson.jpg"),
                    Map.entry("Off the Wall", "Off_the_wall.jpg"),
                    Map.entry("Rock with You", "Off_the_wall.jpg"),
                    Map.entry("Like a Prayer", "Madonna_-_Like_a_Prayer_album.png"),
                    Map.entry("Creep", "pablohoney.jpg"),
                    Map.entry("Fix You", "Coldplay_X&Y.svg"),
                    Map.entry("Rolling in the Deep", "Adele_-_21.png"),
                    Map.entry("Shape of You", "Divide_cover.png"),
                    Map.entry("We Will Rock You", "Queen_News_Of_The_World.png"),
                    Map.entry("I'm in Love with My Car", "Queen_A_Night_At_The_Opera.png"),
                    Map.entry("Here Comes the Sun", "The_Beatles_Abbey_Road_album_cover.jpg")
            );

            Map<String, List<String>> songArtists = Map.ofEntries(
                    Map.entry("Hey Jude", List.of("The Beatles")),
                    Map.entry("Comfortably Numb", List.of("Pink Floyd")),
                    Map.entry("Heroes", List.of("David Bowie")),
                    Map.entry("Bohemian Rhapsody", List.of("Queen")),
                    Map.entry("Billie Jean", List.of("Michael Jackson")),
                    Map.entry("Thriller", List.of("Michael Jackson")),
                    Map.entry("Beat It", List.of("Michael Jackson")),
                    Map.entry("Ben", List.of("Michael Jackson")),
                    Map.entry("Off the Wall", List.of("Michael Jackson")),
                    Map.entry("Rock with You", List.of("Michael Jackson")),
                    Map.entry("Say Say Say", List.of("Michael Jackson", "Paul McCartney")),
                    Map.entry("The Girl is Mine", List.of("Michael Jackson", "Paul McCartney")),
                    Map.entry("Like a Prayer", List.of("Madonna")),
                    Map.entry("Creep", List.of("Radiohead")),
                    Map.entry("Fix You", List.of("Coldplay")),
                    Map.entry("Rolling in the Deep", List.of("Adele")),
                    Map.entry("Shape of You", List.of("Ed Sheeran")),
                    Map.entry("We Will Rock You", List.of("Queen")),
                    Map.entry("I'm in Love with My Car", List.of("Queen")),
                    Map.entry("Here Comes the Sun", List.of("The Beatles"))
            );

            List<Song> songs = csvToBean.parse();

            for (Song song : songs) {
                List<String> artistNames = songArtists.get(song.getTitle());
                if (artistNames != null) {
                    for (String artistName : artistNames) {
                        artistRepository.findArtistByNameIgnoreCase(artistName)
                                .ifPresent(artist -> song.getArtists().add(artist));
                    }
                }

                String filename = songImages.get(song.getTitle());
                if (filename != null) {
                    Image savedImage = null;
                    try {
                        savedImage = imageRepository
                                .save(loadImage("/images/seedData/" + filename, "albumCover"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    song.setAlbumCover(savedImage);
                }
                songRepository.save(song);
            }

    }

    private void userSeed() {
        userRepository.save(new SongScoreUser("Mart", passwordEncoder.encode(martPassword), "ADMIN"));
        userRepository.save(new SongScoreUser("Elias", passwordEncoder.encode(eliasPassword), "USER"));
        userRepository.save(
                new SongScoreUser("Michael", passwordEncoder.encode(michaelPassword), "USER"));
        userRepository.save(
                new SongScoreUser("Yasmine", passwordEncoder.encode(yasminePassword), "USER"));
        userRepository.save(new SongScoreUser("Sophie",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Lucas",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Emma",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Noah",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Lena",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Milan",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Sara",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Daan",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Julia",
                passwordEncoder.encode("userPw"), "USER"));
        userRepository.save(new SongScoreUser("Tom",
                passwordEncoder.encode("userPw"), "USER"));
    }

    private void ratingSeed() {
        ClassPathResource resource = new ClassPathResource("/seedData/ratings.csv");
        Reader reader;
        try {
            reader = new InputStreamReader(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CsvToBean<RatingCsvDTO> csvToBean = new CsvToBeanBuilder<RatingCsvDTO>(reader)
                .withType(RatingCsvDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<RatingCsvDTO> ratingsDto = csvToBean.parse();

        for (RatingCsvDTO dto : ratingsDto) {
            Rating rating = new Rating();
            rating.setRateScore(dto.getRateScore());
            rating.setDescription(dto.getDescription());
            rating.setSong(songService.getSongByTitleAndArtist(dto.getSongTitle(), dto.getArtistName()));
            rating.setRater(songScoreUserService.getUserByUsername(dto.getUsername()));
            ratingRepository.save(rating);
        }

    }

    private Image loadImage(String path, String imageType) throws IOException {
        ClassPathResource resource = new ClassPathResource("/static" + path);

        Image image = new Image();
        image.setData(resource.getInputStream().readAllBytes());
        image.setContentType(resolveContentType(path));
        image.setImageType(imageType);

        return image;
    }

    private String resolveContentType(String filename) {
        if (filename.endsWith(".png")) {
            return "image/png";
        }
        if (filename.endsWith(".avif")) {
            return "image/avif";
        }
        if (filename.endsWith(".jpg")) {
            return "image/jpg";
        }
        if (filename.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (filename.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }
}
