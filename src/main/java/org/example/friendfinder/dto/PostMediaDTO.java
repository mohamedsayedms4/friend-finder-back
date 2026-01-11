package org.example.friendfinder.dto;

import lombok.*;
import org.example.friendfinder.model.MediaType;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PostMediaDTO {
    private Long id;
    private MediaType mediaType;
    private String url;
    private int position;
}
