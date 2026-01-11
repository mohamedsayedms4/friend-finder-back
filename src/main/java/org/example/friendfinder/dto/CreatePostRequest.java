package org.example.friendfinder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.friendfinder.model.PostContentType;
import org.example.friendfinder.model.PostVisibility;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePostRequest {

    @NotNull
    private PostVisibility visibility;

    @NotNull
    private PostContentType contentType;

    private String text;
}
