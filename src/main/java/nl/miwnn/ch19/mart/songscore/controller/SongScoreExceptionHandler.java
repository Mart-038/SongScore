package nl.miwnn.ch19.mart.songscore.controller;

/*
 * @author Mart Stukje
 * Handles exceptions
 * */

import jakarta.persistence.EntityNotFoundException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class SongScoreExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException entityNotFoundException, Model model) {
        model.addAttribute("errorMessage", entityNotFoundException.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ResponseStatusException.class)
    public String handleNotFound(
            ResponseStatusException exception,
            Model model) {
        model.addAttribute("statusCode",
                exception.getStatusCode().value());
        model.addAttribute("errorMessage", exception.getReason());
        return "error/404";
    }

    @ExceptionHandler(MultipartException.class)
    public String handleFileUploadException(MultipartException exception,
                                            Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/500";
    }

}
