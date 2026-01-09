package org.example.friendfinder.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * User account request DTO (email/password fields).
 *
 * @author Mohamed Sayed
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountRequestDTO {

    @Email(message = "user.email.invalid")
    @NotBlank(message = "user.email.required")
    private String email;

    @NotBlank(message = "user.password.required")
    @Size(min = 8, max = 72, message = "user.password.size")
    private String password;

    /**
     * Optional: confirm password (frontend use).
     * If you want strict validation, add a custom class-level validator.
     */
    private String confirmPassword;
}
