package org.example.friendfinder.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Login request payload.
 *
 * @author Mohamed Sayed
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
