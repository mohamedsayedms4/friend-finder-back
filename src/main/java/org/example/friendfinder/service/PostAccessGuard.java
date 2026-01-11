package org.example.friendfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.model.Post;
import org.example.friendfinder.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class PostAccessGuard {

    private final PostRepository postRepository;
    private final FriendsService friendshipService;

    public Post mustGetVisiblePost(Long viewerId, Long postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Long authorId = post.getAuthor().getId();

        // FRIENDS_ONLY policy:
        if (!friendshipService.areFriends(viewerId, authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view this post");
        }

        return post;
    }
}
