// src/main/java/org/example/friendfinder/controller/PostController.java
package org.example.friendfinder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.CreatePostRequest;
import org.example.friendfinder.dto.PostMediaDTO;
import org.example.friendfinder.dto.PostResponseDTO;
import org.example.friendfinder.model.User;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.service.FileStorageService;
import org.example.friendfinder.service.PostAccessGuard;
import org.example.friendfinder.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostAccessGuard guard;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    private Long meId(Authentication auth) {
        String email = auth.getName();
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return u.getId();
    }

    /**
     * Create post with optional media upload.
     *
     * multipart/form-data:
     *  - data: application/json  (CreatePostRequest)
     *  - files: one or many files (images/videos)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDTO> createMultipart(
            Authentication auth,
            @Valid @RequestPart("data") CreatePostRequest req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        Long userId = meId(auth);

        List<PostMediaDTO> uploaded = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            int pos = 0;
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;

                FileStorageService.StoredMedia stored =
                        fileStorageService.storePostMedia(f, userId);

                uploaded.add(PostMediaDTO.builder()
                        .mediaType(stored.mediaType())
                        .url(stored.webPath())
                        .position(pos++)
                        .build());
            }
        }

        PostResponseDTO dto = postService.create(userId, req, uploaded);
        return ResponseEntity.ok(dto);
    }

    // (اختياري) تسيب Endpoint JSON زي ما هو لو محتاجه
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponseDTO> createJson(
            Authentication auth,
            @Valid @RequestBody CreatePostRequest req
    ) {
        PostResponseDTO dto = postService.create(meId(auth), req, List.of());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponseDTO>> feed(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<PostResponseDTO> result =
                postService.feed(meId(auth), PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> get(
            Authentication auth,
            @PathVariable Long id
    ) {
        PostResponseDTO dto = postService.getById(meId(auth), id, guard);
        return ResponseEntity.ok(dto);
    }
}
