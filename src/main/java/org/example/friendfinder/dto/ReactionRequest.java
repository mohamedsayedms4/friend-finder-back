package org.example.friendfinder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.friendfinder.model.ReactionType;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReactionRequest {
    @NotNull
    private ReactionType type;
}
