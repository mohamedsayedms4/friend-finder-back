package org.example.friendfinder.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FriendDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
}
