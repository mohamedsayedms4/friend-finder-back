package org.example.friendfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendRequestDTO {
    private Long id;
    private Long requesterId;
    private Long addresseeId;
    private String status;
}
