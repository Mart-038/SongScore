package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handles requests regarding songs
 * */

import jakarta.validation.Valid;
import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.repository.SongRepository;
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
    private final SongRepository songRepository;

    public SongController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @GetMapping("/all")
    public String showSongOverview(
            @RequestParam(required = false) String query,
            Model model) {
        List<Song> songs = songRepository.findAll();

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

        model.addAttribute("paginatitel", "Nummeroverzicht");
        model.addAttribute("songs", displaySongs);
        model.addAttribute("query", query);
        return "song-overview";
    }

    @GetMapping("/{songId}")
    public String showSongDetailPage(@PathVariable Long songId, Model model) {
        Optional<Song> song = songRepository.findById(songId);
        if (song.isEmpty()) {
            log.warn("Detailpagina aangevraagd voor nummer met id {}, niet gevonden", songId);
            return "redirect:/song/all";
        }

        log.debug("Detailpagina van het nummer {} aangevraagd", song.get().getTitle());

        model.addAttribute("song", song.get());
        return "song-detail";
    }

    @GetMapping("/add")
    public String showSongAddForm(Model model) {
        log.debug("Leeg nummerformulier opgevraagd");
        model.addAttribute("song", new Song());
        return "add-edit-song";
    }

    @PostMapping("/save")
    public String processAddSong(
            @Valid @ModelAttribute Song updatedSong,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        log.info("Nummer opslaan: {}", updatedSong.getTitle());

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}", bindingResult.getErrorCount());
            return "add-edit-song";
        }

        if (updatedSong.getRatings().isEmpty()) {
            updatedSong.getRatings().add(new Rating(1, updatedSong));
            updatedSong.getRatings().add(new Rating(3, updatedSong));
            updatedSong.getRatings().add(new Rating(5, updatedSong));
        }

        songRepository.save(updatedSong);
        log.info("Nieuw nummer toegevoegd: {}", updatedSong.getTitle());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Nummer succesvol toegevoegd!");
        return "redirect:/song/all";
    }

    @GetMapping("/edit/{songId}")
    public String showSongEditForm(@PathVariable Long songId, Model model) {
        log.debug("Bewerkformulier opgevraagd voor: {}", songId);

        Optional<Song> songToEdit = songRepository.findById(songId);
        if (songToEdit.isEmpty()) {
            log.warn("Nummer met ID {} niet gevonden voor bewerking", songId);
            return "redirect:/song/all";
        }
        model.addAttribute("song", songToEdit);
        return "add-edit-song";
    }

    @GetMapping("/delete/{songId}")
    public String deleteSong(@PathVariable Long songId) {
        log.info("Nummer verwijderd: {}", songId);
        songRepository.deleteById(songId);
        return "redirect:/song/all";
    }

}
