package org.example.friendfinder.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Refresh token request payload.
 *
 * @author Mohamed Sayed
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshRequest {

    @NotBlank
    private String refreshToken;
}
