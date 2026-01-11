package org.example.friendfinder.service;

import org.example.friendfinder.dto.MessageDTO;

public interface ChatService {

    MessageDTO send(String senderEmail, Long toUserId, String content);
}
