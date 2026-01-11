// src/main/java/org/example/friendfinder/controller/ReactionController.java
package org.example.friendfinder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.ReactionRequest;
import org.example.friendfinder.model.User;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.service.ReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/reaction")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;
    private final UserRepository userRepository;

    private Long meId(Authentication auth) {
        String email = auth.getName();
        User u = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return u.getId();
    }

    @PutMapping
    public ResponseEntity<Void> upsert(Authentication auth, @PathVariable Long postId, @Valid @RequestBody ReactionRequest req) {
        reactionService.upsert(meId(auth), postId, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(Authentication auth, @PathVariable Long postId) {
        reactionService.remove(meId(auth), postId);
        return ResponseEntity.noContent().build();
    }
}
