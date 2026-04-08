package nl.miwnn.ch19.mart.songscore.dto;

/*
 * @author Mart Stukje
 * Supports new user form
 * */

public class NewSongScoreUserDTO {

    private String username;
    private String plainPassword;
    private String role;

    public NewSongScoreUserDTO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
