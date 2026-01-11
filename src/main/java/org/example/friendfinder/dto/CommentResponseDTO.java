package org.example.friendfinder.dto;

import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommentResponseDTO {
    private Long id;
    private Long postId;
    private Long parentId;

    private UserSummaryDTO author;

    private String content;
    private boolean deleted;

    private Instant createdAt;
}
