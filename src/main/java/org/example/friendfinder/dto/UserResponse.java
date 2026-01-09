package org.example.friendfinder.dto;


import lombok.*;

/**
 * Public user view returned to clients.
 *
 * @author Mohamed Sayed
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String role;
}
