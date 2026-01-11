package org.example.friendfinder.service;

import org.example.friendfinder.dto.MessageDTO;

import java.util.List;

public interface ChatQueryService {
    List<MessageDTO> getMessagesWith(String meEmail, Long otherUserId, int page, int size);
}
