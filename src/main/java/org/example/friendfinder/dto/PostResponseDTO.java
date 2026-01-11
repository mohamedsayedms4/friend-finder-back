package org.example.friendfinder.dto;

import lombok.*;
import org.example.friendfinder.model.PostContentType;
import org.example.friendfinder.model.PostVisibility;
import org.example.friendfinder.model.ReactionType;

import java.time.Instant;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PostResponseDTO {

    private Long id;
    private UserSummaryDTO author;

    private PostVisibility visibility;
    private PostContentType contentType;
    private String text;

    private List<PostMediaDTO> media;

    private long likeCount;
    private long dislikeCount;
    private ReactionType myReaction; // null => NONE

    private long commentCount;

    private Instant createdAt;
}
