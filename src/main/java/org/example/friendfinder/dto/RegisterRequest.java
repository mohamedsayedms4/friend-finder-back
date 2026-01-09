package org.example.friendfinder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Register request (account + profile).
 *
 * Used as JSON part in multipart request under "data".
 *
 * @author Mohamed Sayed
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    // Account
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    // Profile
    @NotBlank(message = "user.fname.required")
    private String firstName;

    @NotBlank(message = "user.lname.required")
    private String lastName;

    private String biography;

    @NotBlank(message = "user.phone.required")
    @Pattern(regexp = "^[0-9+]{8,20}$", message = "user.phone.invalid")
    private String phoneNumber;
}
