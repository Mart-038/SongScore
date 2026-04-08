package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handles html requests regarding users of the SongScore application
 * */

import nl.miwnn.ch19.mart.songscore.dto.NewSongScoreUserDTO;
import nl.miwnn.ch19.mart.songscore.service.SongScoreUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class SongScoreUserController {

    private final Logger log = LoggerFactory.getLogger(SongScoreUserController.class);

    private final SongScoreUserService userService;

    public SongScoreUserController(SongScoreUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public String showUserOverview(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user-overview";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("newUser", new NewSongScoreUserDTO());
        return "add-user";
    }

    @PostMapping("/add")
    public String addUser(
            @ModelAttribute("newUser") NewSongScoreUserDTO dto,
            RedirectAttributes redirectAttributes) {

        userService.saveNewUser(dto);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                String.format("Gebruiker %s aangemaakt", dto.getUsername())
        );
        return "redirect:/user/all";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Gebruiker verwijderd");
        return "redirect:/user/all";
    }
}
