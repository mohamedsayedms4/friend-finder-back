package org.example.friendfinder.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FriendRequestItemDTO {
    private Long requestId;
    private Long userId;

    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;

    private RelationState state; // INCOMING or OUTGOING
}
