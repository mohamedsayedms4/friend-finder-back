package org.example.friendfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.MessageDTO;
import org.example.friendfinder.model.Conversation;
import org.example.friendfinder.model.Message;
import org.example.friendfinder.model.User;
import org.example.friendfinder.repository.ConversationRepository;
import org.example.friendfinder.repository.MessageRepository;
import org.example.friendfinder.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryServiceImpl implements ChatQueryService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final FriendsService friendsService;

    @Override
    public List<MessageDTO> getMessagesWith(String meEmail, Long otherUserId, int page, int size) {

        User me = userRepository.findByEmail(meEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // لو مش أصدقاء رجّع 403 بدل 500
        if (!friendsService.areFriends(me.getId(), otherUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not friends");
        }

        var convOpt = conversationRepository.findBetweenUsers(me.getId(), otherUserId);

        // لو مفيش conversation لسه => مفيش رسائل => []
        if (convOpt.isEmpty()) {
            return List.of();
        }

        Conversation c = convOpt.get();

        var pageable = PageRequest.of(page, size);
        var msgPage = messageRepository.findByConversation_IdOrderByCreatedAtDesc(c.getId(), pageable);

        // ✅ مهم: انسخ الليست لأن getContent() ممكن تكون unmodifiable
        List<Message> desc = new ArrayList<>(msgPage.getContent());
        Collections.reverse(desc); // بقت آمنة

        return desc.stream()
                .map(m -> new MessageDTO(
                        m.getId(),
                        m.getConversation().getId(),
                        m.getSender().getId(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
    }
}
