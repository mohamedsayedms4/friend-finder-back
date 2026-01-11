package org.example.friendfinder.controller;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.OnlineFriendDTO;
import org.example.friendfinder.model.User;
import org.example.friendfinder.model.UserProfile;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.service.FriendsService;
import org.example.friendfinder.service.PresenceService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final FriendsService friendsService;
    private final UserRepository userRepository;
    private final PresenceService presenceService;

    private Long meId(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email).orElseThrow().getId();
    }

    @GetMapping("/friends")
    public List<OnlineFriendDTO> onlineFriends(Authentication auth) {
        Long me = meId(auth);

        // افترض عندك friendsService بيرجع List<User> أو IDs
        List<User> friends = friendsService.listFriendsUsers(me);

        return friends.stream().map(u -> {
            UserProfile p = u.getProfile(); // lazy: الأفضل تعمل DTO query join
            boolean online = presenceService.isOnline(u.getId());
            Instant lastSeen = presenceService.lastPing(u.getId());
            return new OnlineFriendDTO(
                    u.getId(),
                    p != null ? p.getFirstName() : "",
                    p != null ? p.getLastName() : "",
                    p != null ? p.getProfilePicture() : null,
                    online,
                    lastSeen
            );
        }).toList();
    }
}
