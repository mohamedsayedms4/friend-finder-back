package org.example.friendfinder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CreateCommentRequest {

    @NotBlank
    private String content;

    // null => comment on post, else => reply to comment
    private Long parentId;
}
