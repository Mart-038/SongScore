package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handles requests regarding songs
 * */

import jakarta.validation.Valid;
import nl.miwnn.ch19.mart.songscore.dto.SongFormDTO;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import nl.miwnn.ch19.mart.songscore.repository.SongRepository;
import nl.miwnn.ch19.mart.songscore.service.ArtistService;
import nl.miwnn.ch19.mart.songscore.service.ImageService;
import nl.miwnn.ch19.mart.songscore.service.RatingService;
import nl.miwnn.ch19.mart.songscore.service.SongService;
import nl.miwnn.ch19.mart.songscore.service.mapper.SongMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/song")
public class SongController {

    private static final Logger log = LoggerFactory.getLogger(SongController.class);
    private final SongService songService;
    private final ArtistService artistService;
    private final ImageService imageService;
    private final RatingService ratingService;
    private final SongMapper songMapper;

    public SongController(SongService songService,
                          ArtistService artistService,
                          ImageService imageService,
                          RatingService ratingService,
                          SongMapper songMapper) {
        this.songService = songService;
        this.artistService = artistService;
        this.imageService = imageService;
        this.ratingService = ratingService;
        this.songMapper = songMapper;
    }

    @GetMapping("/all")
    public String showSongOverview(
            @RequestParam(required = false) String query,
            Model model) {
        List<Song> displaySongs;

        if (query != null && !query.isBlank()) {
            log.debug("Zoeken op query: {}", query);
            displaySongs = songService.searchSongsByTitleContaining(query);
        } else {
            displaySongs = songService.getAllSongs();
        }

        log.debug("Song overview opgevraagd, {} songs opgehaald", displaySongs.size());
        model.addAttribute("activePage", "songs");
        model.addAttribute("songs", displaySongs);
        model.addAttribute("query", query);
        return "song-overview";
    }

    @GetMapping("/add")
    public String showSongAddForm(Model model) {
        log.debug("Leeg nummerformulier opgevraagd");
        model.addAttribute("songForm", new SongFormDTO());
        model.addAttribute("albumCovers", imageService.getImagesUsedFor("albumCover"));
        model.addAttribute("allArtists", artistService.getAllArtists());
        return "add-edit-song";
    }

    @GetMapping("/edit/{artistName}/{title}")
    public String showSongEditForm(@PathVariable String artistName, @PathVariable String title, Model model) {
        log.debug("Bewerkformulier opgevraagd voor: {}", title);
        Song songToEdit = songService.getSongByTitleAndArtist(title, artistName);
        SongFormDTO toEditDto = songMapper.toDto(songToEdit);

        model.addAttribute("songForm", toEditDto);
        model.addAttribute("allArtists", artistService.getAllArtists());
        return "add-edit-song";
    }

    @PostMapping("/save")
    public String processAddSong(@Valid @ModelAttribute("songForm") SongFormDTO dto,
                                 BindingResult bindingResult,
                                 @RequestParam("imageFile") MultipartFile albumCover,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        log.info("Nummer opslaan: {}", dto.getTitle());

        if (songService.songAndArtistCombinationExists(dto)) {
                log.warn("Not updating song, combination of title and first artist already exists");
                bindingResult.reject("alreadyExists",
                        "Deze combinatie van titel en artiest bestaat al");
            }

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}", bindingResult.getErrorCount());
            model.addAttribute("allArtists", artistService.getAllArtists());
            return "add-edit-song";
        }

        songService.saveSong(dto, albumCover);
        log.info("Nummer opgeslagen: {}", dto.getTitle());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Nummer succesvol opgeslagen!");
        return "redirect:/song/all";
    }

    @GetMapping("/delete/{artistName}/{title}")
    public String deleteSong(@PathVariable String artistName,
                             @PathVariable String title,
                             RedirectAttributes redirectAttributes) {
        Song songToDelete = songService.getSongByTitleAndArtist(title, artistName);

        log.info("Nummer verwijderd: {}, {}", title, artistName);
        songService.deleteSong(songToDelete);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Nummer succesvol verwijderd!");
        return "redirect:/song/all";
    }

    @GetMapping("/detail/{artistName}/{title}")
    public String showSongDetailPage(@PathVariable String artistName,
                                     @PathVariable String title, Model model,
                                     Authentication authentication) {
        Song song = songService.getSongByTitleAndArtist(title, artistName);

        SongScoreUser currentUser = null;
        boolean hasRatedSong = false;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = (SongScoreUser) authentication.getPrincipal();
            hasRatedSong = ratingService.raterAndSongCombinationAlreadyExists(song, currentUser);
        }


        log.debug("Detailpagina van het nummer {} aangevraagd", song.getTitle());
        model.addAttribute("song", song);
        model.addAttribute("newRating", new Rating());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("hasRated", hasRatedSong);
        return "song-detail";
    }

}
