package org.example.friendfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.ReactionRequest;
import org.example.friendfinder.model.Post;
import org.example.friendfinder.model.PostReaction;
import org.example.friendfinder.model.ReactionType;
import org.example.friendfinder.repository.PostReactionRepository;
import org.example.friendfinder.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final PostReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final PostAccessGuard guard;

    @Transactional
    public void upsert(Long meId, Long postId, ReactionRequest req) {
        Post post = guard.mustGetVisiblePost(meId, postId);

        PostReaction reaction = reactionRepository.findByPost_IdAndUser_Id(postId, meId)
                .orElseGet(() -> PostReaction.builder()
                        .post(post)
                        .user(userRepository.findById(meId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                        .build());

        ReactionType type = req.getType();
        if (type == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reaction type is required");

        reaction.setType(type);
        reactionRepository.save(reaction);
    }

    @Transactional
    public void remove(Long meId, Long postId) {
        // ensure can view (friends-only)
        guard.mustGetVisiblePost(meId, postId);
        reactionRepository.deleteByPost_IdAndUser_Id(postId, meId);
    }
}
