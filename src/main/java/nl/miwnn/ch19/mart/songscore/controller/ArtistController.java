package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handles requests regarding artists
 * */

import jakarta.validation.Valid;
import nl.miwnn.ch19.mart.songscore.dto.ArtistFormDTO;
import nl.miwnn.ch19.mart.songscore.model.Artist;
import nl.miwnn.ch19.mart.songscore.repository.ArtistRepository;
import nl.miwnn.ch19.mart.songscore.repository.ImageRepository;
import nl.miwnn.ch19.mart.songscore.service.ArtistService;
import nl.miwnn.ch19.mart.songscore.service.mapper.ArtistMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/artist")
public class ArtistController {

    private static final Logger log = LoggerFactory.getLogger(ArtistController.class);
    private final ArtistMapper artistMapper;
    private final ArtistService artistService;

    public ArtistController(ArtistMapper artistMapper, ArtistService artistService) {
        this.artistMapper = artistMapper;
        this.artistService = artistService;
    }

    @GetMapping("/all")
    public String showArtistOverview(Model model) {
        log.debug("Showing artist overview");
        List<Artist> artists = artistService.getAllArtists();
        model.addAttribute("artists", artists);
        model.addAttribute("artistForm", new ArtistFormDTO());
        model.addAttribute("activePage", "artists");
        return "artist-overview";
    }

    @GetMapping("/add")
    public String showArtistAddForm(Model model) {
        log.debug("Showing artist add form");
        model.addAttribute("artistForm", new ArtistFormDTO());
        return "add-edit-artist";
    }

    @GetMapping("/edit/{artistName}")
    public String showArtistEditForm(@PathVariable String artistName, Model model) {
        log.debug("Bewerkformulier voor artiest {} wordt opgevraagd", artistName);
        Artist artist = artistService.getArtistByName(artistName);
        ArtistFormDTO dto = artistMapper.toFormDto(artist);
        model.addAttribute("artistForm", dto);

        return "add-edit-artist";
    }

    @PostMapping("/save")
    public String processAddArtist(@Valid @ModelAttribute("artistForm") ArtistFormDTO artistForm,
                                   BindingResult bindingResult,
                                   @RequestParam("imageFile") MultipartFile imageFile,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        log.info("Artiest opslaan: {}", artistForm.getName());

        if (artistService.artistNameAlreadyInUse(artistForm.getName(), artistForm.getId())) {
                log.warn("Updating artist, name already exists in DB so should not be allowed");
                bindingResult.rejectValue(
                        "name",
                        "alreadyExists",
                        "Deze naam komt al voor in de lijst");
            }

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan artiest: {}", bindingResult.getErrorCount());
            List<Artist> artists = artistService.getAllArtists();
            model.addAttribute("artists", artists);
            model.addAttribute("showNewArtistModal", true);
            return "artist-overview";
        }

        artistService.saveArtist(artistForm, imageFile);
        log.info("Artiest succesvol toegevoegd: {}", artistForm.getName());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Artiest succesvol toegevoegd!");
        return "redirect:/artist/all";
    }

    @GetMapping("/delete/{artistName}")
    public String deleteArtist(@PathVariable String artistName) {
        log.info("Artiest verwijderd uit de database met naam: {}", artistName);
        artistService.deleteArtistByName(artistName);
        return "redirect:/artist/all";
    }

    @GetMapping("/detail/{artistName}")
    public String showArtistDetailPage(@PathVariable String artistName, Model model) {
        Artist artist = artistService.getArtistByName(artistName);
        model.addAttribute("artist", artist);
        return "artist-detail";
    }
}
