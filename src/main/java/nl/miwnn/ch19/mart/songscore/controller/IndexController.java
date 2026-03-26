package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handle requests
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/")
    public String showIndexPage(Model model) {
        log.debug("Indexpagina opgevraagd");
        model.addAttribute("naam", "Mart");
        model.addAttribute("datum", LocalDate.now().toString());
        log.info("Startpagina getoond om {}", LocalTime.now());
        return "index";
    }

}
