package org.example.friendfinder.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.friendfinder.dto.UserMeResponseDTO;
import org.example.friendfinder.dto.UserProfileRequestDTO;
import org.example.friendfinder.model.UserProfile;
import org.example.friendfinder.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Operation(summary = "Create/Update user profile with image upload")
    @PostMapping(value = "/{userId}/profile", consumes = "multipart/form-data")
    public ResponseEntity<UserProfile> upsertProfile(
            @PathVariable Long userId,
            @Valid @RequestPart("data") UserProfileRequestDTO data,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(userProfileService.upsertProfile(userId, data, image));
    }

    @Operation(summary = "Get current logged-in user (from JWT token)")
    @GetMapping("/me")
    public ResponseEntity<UserMeResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(userProfileService.getMe(authentication.getName()));
    }

    @Operation(summary = "Get users suggestions")
    @GetMapping("/suggestions")
    public ResponseEntity<?> suggestions(
            Authentication authentication,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(userProfileService.getSuggestions(authentication.getName(), limit));
    }

}
