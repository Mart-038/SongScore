package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handles requests regarding ratings
 * */

import jakarta.validation.Valid;
import nl.miwnn.ch19.mart.songscore.model.Rating;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import nl.miwnn.ch19.mart.songscore.service.RatingService;
import nl.miwnn.ch19.mart.songscore.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/rating")
public class RatingController {

    private final Logger log = LoggerFactory.getLogger(RatingController.class);

    private final RatingService ratingService;
    private final SongService songService;

    public RatingController(RatingService ratingService, SongService songService) {
        this.ratingService = ratingService;
        this.songService = songService;
    }

    @PostMapping("/song/{songId}/save")
    public String processRatingForm(@Valid @ModelAttribute("newRating") Rating newRating,
                                    BindingResult bindingResult,
                                    @PathVariable Long songId,
                                    Model model,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {

        Song song = songService.getSongById(songId);
        newRating.setSong(song);
        log.debug("Nieuwe rating voor nummer {}", newRating.getSong().getTitle());

        SongScoreUser currentUser = null;
        boolean hasRatedSong = false;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = (SongScoreUser) authentication.getPrincipal();
            hasRatedSong = ratingService.raterAndSongCombinationAlreadyExists(song, currentUser);
        }

        newRating.setRater(currentUser);
        newRating.setPlacedAt(LocalDateTime.now());

        if (ratingService.raterAndSongCombinationAlreadyExists(newRating.getSong(), newRating.getRater())) {
            log.warn("User is rating song that he already rated");
            bindingResult.reject("alreadyExists", "Je hebt dit nummer al een rating gegeven");
        }

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}", bindingResult.getErrorCount());
            model.addAttribute("song", song);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("hasRated", hasRatedSong);
            model.addAttribute("newRating", newRating);
            return "song-detail";
        }

        ratingService.saveRating(newRating);
        log.info("Rating opgeslagen bij nummer: {}", newRating.getSong().getTitle());
        redirectAttributes.addFlashAttribute("successMessage", "Rating ingestuurd!");
        String redirectUrl = UriComponentsBuilder.fromPath("/song/detail/{artistName}/{title}")
                .buildAndExpand(newRating.getSong().getArtists().get(0).getName(), newRating.getSong().getTitle())
                .toUriString();
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/delete/{ratingId}")
    public String deleteRating(@PathVariable Long ratingId, RedirectAttributes redirectAttributes) {
        Rating ratingToDelete = ratingService.getRatingById(ratingId);
        String redirectUrl = UriComponentsBuilder.fromPath("/song/detail/{artistName}/{title}")
                .buildAndExpand(
                        ratingToDelete.getSong().getArtists().get(0).getName(),
                        ratingToDelete.getSong().getTitle())
                .toUriString();
        ratingService.deleteRating(ratingToDelete);
        log.info("Rating verwijderd met id {}", ratingId);
        redirectAttributes.addFlashAttribute("successMessage",
                "Je rating voor dit nummer is verwijderd");
        return "redirect:" + redirectUrl;
    }

}
