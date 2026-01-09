package org.example.friendfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRelationDTO {
    private Long userId;
    private RelationState state;
    private Long requestId;
}
