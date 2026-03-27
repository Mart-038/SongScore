package nl.miwnn.ch19.mart.songscore.model;

/*
 * @author Mart Stukje
 * A musician or a group of musicians that have recorded one or multiple songs
 * */

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "De naam mag niet leeg zijn")
    @Size(max = 200, message = "De naam mag niet meer dan {max} tekens bevatten")
    @Column(unique = true)
    private String name;

    @NotNull(message = "Het jaar mag niet leeg zijn")
    @Min(value = 1000, message = "Het jaar mag niet lager dan {value} zijn")
    @Max(value = 2030, message = "Het jaar mag niet hoger dan {value} zijn")
    private Integer activeSince;

    public Artist() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getActiveSince() {
        return activeSince;
    }

    public void setActiveSince(Integer activeSince) {
        this.activeSince = activeSince;
    }
}
