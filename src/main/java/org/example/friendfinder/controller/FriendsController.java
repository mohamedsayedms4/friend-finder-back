package org.example.friendfinder.controller;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.FriendDTO;
import org.example.friendfinder.dto.FriendRequestItemDTO;
import org.example.friendfinder.dto.UserRelationDTO;
import org.example.friendfinder.model.User;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.service.FriendsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;
    private final UserRepository userRepository;

    private Long meId(Authentication auth) {
        String email = auth.getName();
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return u.getId();
    }

    // ✅ إرسال طلب صداقة
    @PostMapping("/requests/{targetId}")
    public ResponseEntity<Void> send(Authentication auth, @PathVariable Long targetId) {
        friendsService.sendRequest(meId(auth), targetId);
        return ResponseEntity.ok().build();
    }

    // ✅ إلغاء طلب صداقة (Outgoing)
    @DeleteMapping("/requests/{targetId}")
    public ResponseEntity<Void> cancel(Authentication auth, @PathVariable Long targetId) {
        friendsService.cancelOutgoing(meId(auth), targetId);
        return ResponseEntity.ok().build();
    }

    // ✅ قبول طلب صداقة (Incoming) باستخدام requestId
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<Void> accept(Authentication auth, @PathVariable Long requestId) {
        friendsService.accept(meId(auth), requestId);
        return ResponseEntity.ok().build();
    }

    // ✅ رفض طلب صداقة (Incoming) باستخدام requestId
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> reject(Authentication auth, @PathVariable Long requestId) {
        friendsService.reject(meId(auth), requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests/incoming")
    public ResponseEntity<List<FriendRequestItemDTO>> incoming(Authentication auth) {
        return ResponseEntity.ok(friendsService.incomingRequestItems(meId(auth)));
    }

    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<FriendRequestItemDTO>> outgoing(Authentication auth) {
        return ResponseEntity.ok(friendsService.outgoingRequestItems(meId(auth)));
    }

    @GetMapping("/friends")
    public ResponseEntity<List<FriendDTO>> friends(Authentication auth) {
        return ResponseEntity.ok(friendsService.friends(meId(auth)));
    }

    @DeleteMapping("/friends/{otherId}")
    public ResponseEntity<Void> unfriend(Authentication auth, @PathVariable Long otherId) {
        friendsService.unfriend(meId(auth), otherId);
        return ResponseEntity.ok().build();
    }

    // ✅ Block / Unblock
    @PostMapping("/block/{otherId}")
    public ResponseEntity<Void> block(Authentication auth, @PathVariable Long otherId) {
        friendsService.block(meId(auth), otherId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/block/{otherId}")
    public ResponseEntity<Void> unblock(Authentication auth, @PathVariable Long otherId) {
        friendsService.unblock(meId(auth), otherId);
        return ResponseEntity.ok().build();
    }

    // ✅ معرفة حالة العلاقة بيني وبين شخص
    @GetMapping("/relation/{otherId}")
    public ResponseEntity<UserRelationDTO> relation(Authentication auth, @PathVariable Long otherId) {
        return ResponseEntity.ok(friendsService.relation(meId(auth), otherId));
    }
}
