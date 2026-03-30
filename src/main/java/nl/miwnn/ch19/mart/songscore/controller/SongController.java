package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handles requests regarding songs
 * */

import jakarta.validation.Valid;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import nl.miwnn.ch19.mart.songscore.repository.SongRepository;
import nl.miwnn.ch19.mart.songscore.service.ArtistService;
import nl.miwnn.ch19.mart.songscore.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/song")
public class SongController {

    private static final Logger log = LoggerFactory.getLogger(SongController.class);
    private final SongService songService;
    private final ArtistService artistService;

    public SongController(SongService songService, ArtistService artistService) {
        this.songService = songService;
        this.artistService = artistService;
    }

    @GetMapping("/all")
    public String showSongOverview(
            @RequestParam(required = false) String query,
            Model model) {
        List<Song> songs = songService.getAllSongs();

        log.debug("Song overview opgevraagd, {} songs opgehaald", songs.size());

        List<Song> displaySongs;

        if (query != null && !query.isBlank()) {
            log.debug("Zoeken op query: {}", query);
            displaySongs = songs.stream()
                    .filter(song -> song.getTitle()
                            .toLowerCase()
                            .contains(query.toLowerCase()))
                    .toList();
        } else {
            displaySongs = songs;
        }

        model.addAttribute("activePage", "songs");
        model.addAttribute("songs", displaySongs);
        model.addAttribute("query", query);
        return "song-overview";
    }

    @GetMapping("/add")
    public String showSongAddForm(Model model) {
        log.debug("Leeg nummerformulier opgevraagd");
        model.addAttribute("song", new Song());
        model.addAttribute("allArtists", artistService.getAllArtists());
        return "add-edit-song";
    }

    @GetMapping("/edit/{artistName}/{title}")
    public String showSongEditForm(@PathVariable String artistName, @PathVariable String title, Model model) {
        log.debug("Bewerkformulier opgevraagd voor: {}", title);
        Song songToEdit = songService.getSongByTitleAndArtist(title, artistName);

        model.addAttribute("song", songToEdit);
        model.addAttribute("allArtists", artistService.getAllArtists());
        return "add-edit-song";
    }

    @PostMapping("/save")
    public String processAddSong(
            @Valid @ModelAttribute Song updatedSong,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Nummer opslaan: {}", updatedSong.getTitle());

        if (songService.songAndArtistCombinationExists(updatedSong)) {
                log.warn("Not updating song, combination of title and first artist already exists");
                bindingResult.reject("alreadyExists",
                        "Deze combinatie van titel en artiest bestaat al");
            }

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}", bindingResult.getErrorCount());
            model.addAttribute("allArtists", artistService.getAllArtists());
            return "add-edit-song";
        }

        songService.saveSong(updatedSong);
        log.info("Nummer opgeslagen: {}", updatedSong.getTitle());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Nummer succesvol opgeslagen!");
        return "redirect:/song/all";
    }

    @GetMapping("/delete/{artistName}/{title}")
    public String deleteSong(@PathVariable String artistName, @PathVariable String title) {
        Song songToDelete = songService.getSongByTitleAndArtist(title, artistName);

        log.info("Nummer verwijderd: {}, {}", title, artistName);
        songService.deleteSong(songToDelete);
        return "redirect:/song/all";
    }

    @GetMapping("/{artistName}/{title}")
    public String showSongDetailPage(@PathVariable String artistName, @PathVariable String title, Model model) {
        Song song = songService.getSongByTitleAndArtist(title, artistName);

        log.debug("Detailpagina van het nummer {} aangevraagd", song.getTitle());
        model.addAttribute("song", song);
        return "song-detail";
    }

}
