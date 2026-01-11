package org.example.friendfinder.dto;

import java.time.Instant;

public record MessageDTO(Long id, Long conversationId, Long senderId, String content, Instant createdAt) {}
