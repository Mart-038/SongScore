package nl.miwnn.ch19.mart.songscore.repository;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
