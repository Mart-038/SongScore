package nl.miwnn.ch19.mart.songscore.service.mapper;

/*
 * @author Mart Stukje
 * */

import nl.miwnn.ch19.mart.songscore.dto.NewSongScoreUserDTO;
import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SongScoreUserMapper {

    public SongScoreUser toSongScoreUser(
            NewSongScoreUserDTO dto,
            PasswordEncoder passwordEncoder) {

        SongScoreUser user = new SongScoreUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPlainPassword()));
        user.setRole(dto.getRole());
        return user;
    }
}
