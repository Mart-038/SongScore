package nl.miwnn.ch19.mart.songscore.dto;

/*
 * @author Mart Stukje
 * Supports new/edit artist form
 * */


public class ArtistFormDTO {

    private Long id;
    private String name;
    private String bio;
    private Integer activeSince;
    private Integer activeUntil;
    private Long existingImageId;

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getActiveSince() {
        return activeSince;
    }

    public void setActiveSince(Integer activeSince) {
        this.activeSince = activeSince;
    }

    public Integer getActiveUntil() {
        return activeUntil;
    }

    public void setActiveUntil(Integer activeUntil) {
        this.activeUntil = activeUntil;
    }

    public Long getExistingImageId() {
        return existingImageId;
    }

    public void setExistingImageId(Long existingImageId) {
        this.existingImageId = existingImageId;
    }
}
