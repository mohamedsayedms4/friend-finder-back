package org.example.friendfinder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.CommentResponseDTO;
import org.example.friendfinder.dto.CreateCommentRequest;
import org.example.friendfinder.model.User;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    private Long meId(Authentication auth) {
        String email = auth.getName();
        User u = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return u.getId();
    }

    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDTO> add(
            Authentication auth,
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest req
    ) {
        return ResponseEntity.ok(commentService.add(meId(auth), postId, req));
    }

    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponseDTO>> list(
            Authentication auth,
            @PathVariable Long postId,
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        if (parentId == null) {
            return ResponseEntity.ok(commentService.listTopLevel(meId(auth), postId, PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(commentService.listReplies(meId(auth), postId, parentId, PageRequest.of(page, size)));
    }

    @DeleteMapping("/api/v1/comments/{commentId}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long commentId) {
        commentService.delete(meId(auth), commentId);
        return ResponseEntity.noContent().build();
    }
}
