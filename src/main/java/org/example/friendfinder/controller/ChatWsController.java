package org.example.friendfinder.controller;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.MessageDTO;
import org.example.friendfinder.dto.SendMessageRequest;
import org.example.friendfinder.model.User;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void send(Authentication auth, SendMessageRequest req) {
        if (auth == null) {
            throw new IllegalStateException("WS unauthenticated: missing Authorization header on CONNECT");
        }

        String senderEmail = auth.getName();

        MessageDTO saved = chatService.send(senderEmail, req.toUserId(), req.content());

        // هات إيميل المستقبل عشان نبعته على user destination الصح
        User receiver = userRepository.findById(req.toUserId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        String receiverEmail = receiver.getEmail();

        // ✅ إرسال للمرسل (Principal = email)
        messagingTemplate.convertAndSendToUser(
                senderEmail,
                "/queue/messages",
                saved
        );

        // ✅ إرسال للمستقبل (Principal = email)
        messagingTemplate.convertAndSendToUser(
                receiverEmail,
                "/queue/messages",
                saved
        );
    }
}
