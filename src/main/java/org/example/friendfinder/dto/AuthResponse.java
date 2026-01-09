package org.example.friendfinder.dto;


import lombok.*;

/**
 * Authentication response containing issued tokens.
 *
 * @author Mohamed Sayed
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String tokenType;        // "Bearer"
    private String accessToken;
    private String refreshToken;
    private long accessExpiresInSec;
    private long refreshExpiresInSec;
    private UserResponse user;
}
