package org.example.friendfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.MessageDTO;
import org.example.friendfinder.model.Conversation;
import org.example.friendfinder.model.Message;
import org.example.friendfinder.model.User;
import org.example.friendfinder.repository.ConversationRepository;
import org.example.friendfinder.repository.MessageRepository;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.service.ChatService;
import org.example.friendfinder.service.FriendsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final FriendsService friendsService;

    @Override
    public MessageDTO send(String senderEmail, Long toUserId, String content) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // تأكد إنهم أصدقاء
        if (!friendsService.areFriends(sender.getId(), toUserId)) {
            throw new RuntimeException("Users are not friends");
        }

        User receiver = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Conversation conversation = conversationRepository
                .findBetweenUsers(sender.getId(), receiver.getId())
                .orElseGet(() -> {
                    Conversation c = Conversation.builder()
                            .user1(sender.getId() < receiver.getId() ? sender : receiver)
                            .user2(sender.getId() < receiver.getId() ? receiver : sender)
                            .createdAt(Instant.now())
                            .build();
                    return conversationRepository.save(c);
                });

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .createdAt(Instant.now())
                .build();

        Message saved = messageRepository.save(message);

        return new MessageDTO(
                saved.getId(),
                conversation.getId(),
                sender.getId(),
                saved.getContent(),
                saved.getCreatedAt()
        );
    }
}
