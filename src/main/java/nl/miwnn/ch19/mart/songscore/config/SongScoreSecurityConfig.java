package nl.miwnn.ch19.mart.songscore.config;

/*
 * @author Mart Stukje
 * Configure the security for SongScore
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SongScoreSecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SongScoreSecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/song/all",
                                "/artist/all",
                                "/song/detail/**",
                                "/artist/detail/**",
                                "/image/**",
                                "/css/**",
                                "/webjars/**",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers(
                                "/song/edit/**"
                        ).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(
                                "/artist/edit/**",
                                "/user/**",
                                "/song/add",
                                "/artist/add"
                                )
                        .hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/song/all")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/song/all")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
