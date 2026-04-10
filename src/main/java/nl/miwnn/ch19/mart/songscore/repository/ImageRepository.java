package nl.miwnn.ch19.mart.songscore.repository;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByImageTypeIs(String imageType);
}
