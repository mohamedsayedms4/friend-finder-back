package org.example.friendfinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.friendfinder.dto.UserMeResponseDTO;
import org.example.friendfinder.dto.UserProfileRequestDTO;
import org.example.friendfinder.dto.UserSuggestionDTO;
import org.example.friendfinder.model.User;
import org.example.friendfinder.model.UserProfile;
import org.example.friendfinder.repository.UserProfileRepository;
import org.example.friendfinder.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public UserProfile upsertProfile(Long userId, UserProfileRequestDTO dto, MultipartFile image) {
        log.info("Upsert profile attempt: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile profile = profileRepository.findById(userId)
                .orElseGet(() -> UserProfile.builder().user(user).build());

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setBiography(dto.getBiography());
        profile.setPhoneNumber(dto.getPhoneNumber());

        String storedPath = fileStorageService.storeProfilePicture(image, userId);
        if (storedPath != null) {
            profile.setProfilePicture(storedPath);
        }

        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public UserMeResponseDTO getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // حسب اسم الميثود عندك في الريبو:
        // لو عندك: Optional<UserProfile> findByUserId(Long userId)
        UserProfile profile = profileRepository.findByUserId(user.getId()).orElse(null);

        String firstName = (profile != null) ? profile.getFirstName() : null;
        String lastName  = (profile != null) ? profile.getLastName() : null;
        String picture   = (profile != null) ? profile.getProfilePicture() : null;

        return new UserMeResponseDTO(user.getId(), user.getEmail(), firstName, lastName, picture);
    }

    public List<UserSuggestionDTO> getSuggestions(String myEmail, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 20));
        return userRepository.findSuggestions(myEmail, PageRequest.of(0, safeLimit));
    }
}
