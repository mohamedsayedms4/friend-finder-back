package org.example.friendfinder.dto;

public record UserSuggestionDTO(
        Long userId,
        String email,
        String firstName,
        String lastName,
        String profilePicture
) {}
