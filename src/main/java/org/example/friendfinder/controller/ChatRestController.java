package org.example.friendfinder.controller;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.MessageDTO;
import org.example.friendfinder.service.ChatQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatQueryService chatQueryService;

    @GetMapping("/conversations/with/{userId}/messages")
    public List<MessageDTO> messagesWith(Authentication auth,
                                         @PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "30") int size) {
        return chatQueryService.getMessagesWith(auth.getName(), userId, page, size);
    }
}
