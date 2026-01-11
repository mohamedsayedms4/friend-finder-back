package org.example.friendfinder.dto;

import java.time.Instant;

public record OnlineFriendDTO(
        Long userId,
        String firstName,
        String lastName,
        String profilePicture,
        boolean online,
        Instant lastSeen
) {}
