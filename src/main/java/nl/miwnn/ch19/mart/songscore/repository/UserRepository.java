package nl.miwnn.ch19.mart.songscore.repository;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SongScoreUser, Long> {

    Optional<SongScoreUser> findByUsername(String username);
}
