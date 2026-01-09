package org.example.friendfinder.dto;

public class UserProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePicture;

    public UserProfileResponseDTO() {}

    public UserProfileResponseDTO(Long id, String firstName, String lastName, String profilePicture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getProfilePicture() { return profilePicture; }

    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}
