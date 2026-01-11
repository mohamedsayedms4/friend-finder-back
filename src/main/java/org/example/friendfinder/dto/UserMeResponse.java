package org.example.friendfinder.dto;

public record UserMeResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String profilePicture
) {}
