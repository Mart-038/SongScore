package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handles requests regarding artists
 * */

import jakarta.validation.Valid;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.model.Song;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/artist")
public class ArtistController {

    private static final Logger log = LoggerFactory.getLogger(ArtistController.class);
    private final ArtistRepository artistRepository;

    public ArtistController(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @GetMapping("/all")
    public String showArtistOverview(Model model) {
        log.debug("Showing artist overview");
        List<Artist> artists = artistRepository.findAll();
        model.addAttribute("artists", artists);
        return "artist-overview";
    }

    @GetMapping("/add")
    public String showArtistAddForm(Model model) {
        log.debug("Showing artist add form");
        model.addAttribute(new Artist());
        return "add-edit-artist";
    }

    @PostMapping("/save")
    public String processAddArtist(@Valid @ModelAttribute Artist updatedArtist,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        log.info("Artiest opslaan: {}", updatedArtist.getName());

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan artiest: {}", bindingResult.getErrorCount());
            return "add-edit-artist";
        }

        artistRepository.save(updatedArtist);
        log.info("Artiest succesvol toegevoegd: {}", updatedArtist.getName());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Artiest succesvol toegevoegd!");
        return "redirect:/artist/all";
    }

    @GetMapping("/delete/{artistId}")
    public String deleteArtist(@PathVariable Long artistId) {
        log.info("Artiest verwijderd uit de database met ID: {}", artistId);
        artistRepository.deleteById(artistId);
        return "redirect:/artist/all";
    }
}
