package nl.miwnn.ch19.mart.songscore.service;

/*
 * @author Mart Stukje
 * Handles business logic regarding SongScore users
 * */

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.mart.songscore.dto.NewSongScoreUserDTO;
import nl.miwnn.ch19.mart.songscore.model.SongScoreUser;
import nl.miwnn.ch19.mart.songscore.repository.UserRepository;
import nl.miwnn.ch19.mart.songscore.service.mapper.SongScoreUserMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongScoreUserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SongScoreUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public SongScoreUserService(UserRepository userRepository,
                                SongScoreUserMapper userMapper,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException(
                        "Gebruiker niet gevonden met username: " + username));
    }

    public void saveNewUser(NewSongScoreUserDTO dto) {
        SongScoreUser songScoreUser = userMapper.toSongScoreUser(dto, passwordEncoder);
        userRepository.save(songScoreUser);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public List<SongScoreUser> getAllUsers() {
        return userRepository.findAll();
    }

    public SongScoreUser getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Gebruiker niet gevonden in database"));
    }
}
