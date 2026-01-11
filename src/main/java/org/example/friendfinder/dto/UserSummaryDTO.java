package org.example.friendfinder.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profileImageUrl; // optional (لو عندك)
}
