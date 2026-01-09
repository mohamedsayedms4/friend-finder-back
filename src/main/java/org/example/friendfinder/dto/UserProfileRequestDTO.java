package org.example.friendfinder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User profile request payload (JSON part in multipart request).
 *
 * @author Mohamed Sayed
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequestDTO {

    @NotBlank(message = "user.fname.required")
    private String firstName;

    @NotBlank(message = "user.lname.required")
    private String lastName;

    private String biography;

    /**
     * Stored path or URL of the uploaded profile picture.
     * Set by server after file upload.
     */
    private String profilePicture;

    @NotBlank(message = "user.phone.required")
    @Pattern(
            regexp = "^[0-9+]{8,20}$",
            message = "user.phone.invalid"
    )
    private String phoneNumber;

    @Valid
    private UserAccountRequestDTO accountDTO;
}
