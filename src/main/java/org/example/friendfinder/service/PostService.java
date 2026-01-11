package org.example.friendfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.PostMediaDTO;
import org.example.friendfinder.dto.PostResponseDTO;
import org.example.friendfinder.dto.UserSummaryDTO;
import org.example.friendfinder.dto.CreatePostRequest;
import org.example.friendfinder.model.*;
import org.example.friendfinder.repository.PostCommentRepository;
import org.example.friendfinder.repository.PostReactionRepository;
import org.example.friendfinder.repository.PostRepository;
import org.example.friendfinder.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendsService friendshipService;

    private final PostReactionRepository reactionRepository;
    private final PostCommentRepository commentRepository;

    @Transactional
    public PostResponseDTO create(Long meId, CreatePostRequest req, List<PostMediaDTO> uploadedMedia) {
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Post post = Post.builder()
                .author(me)
                .visibility(req.getVisibility())
                .contentType(req.getContentType())
                .text(req.getText())
                .build();

        // attach media
        if (uploadedMedia != null && !uploadedMedia.isEmpty()) {
            int pos = 0;
            for (PostMediaDTO m : uploadedMedia) {
                post.addMedia(PostMedia.builder()
                        .mediaType(m.getMediaType())
                        .url(m.getUrl())
                        .position(pos++)
                        .build());
            }
        }

        Post saved = postRepository.save(post);
        return toDto(meId, saved);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> feed(Long meId, Pageable pageable) {
        Set<Long> friends = friendshipService.friendIds(meId);
        friends.add(meId);

        Page<Post> page = postRepository.findByDeletedFalseAndVisibilityAndAuthor_IdInOrderByCreatedAtDesc(
                PostVisibility.FRIENDS_ONLY,
                friends,
                pageable
        );

        return page.map(p -> toDto(meId, p));
    }

    @Transactional(readOnly = true)
    public PostResponseDTO getById(Long meId, Long postId, PostAccessGuard guard) {
        Post post = guard.mustGetVisiblePost(meId, postId);
        return toDto(meId, post);
    }

    private PostResponseDTO toDto(Long meId, Post post) {
        long likeCount = reactionRepository.countByPost_IdAndType(post.getId(), ReactionType.LIKE);
        long dislikeCount = reactionRepository.countByPost_IdAndType(post.getId(), ReactionType.DISLIKE);
        long commentCount = commentRepository.countByPost_Id(post.getId());

        ReactionType myReaction = reactionRepository.findByPost_IdAndUser_Id(post.getId(), meId)
                .map(PostReaction::getType)
                .orElse(null);

        // ---- media list ----
        List<PostMediaDTO> media = new ArrayList<>();
        if (post.getMedia() != null) {
            for (PostMedia m : post.getMedia()) {
                media.add(PostMediaDTO.builder()
                        .id(m.getId())
                        .mediaType(m.getMediaType())
                        .url(m.getUrl())
                        .position(m.getPosition())
                        .build());
            }
        }

        // ---- author summary (NOT NULL now) ----
        User author = post.getAuthor();
        UserProfile profile = (author != null ? author.getProfile() : null);

        String firstName = (profile != null ? profile.getFirstName() : null);
        String lastName  = (profile != null ? profile.getLastName() : null);
        String profileImageUrl = (profile != null ? profile.getProfilePicture() : null);

        UserSummaryDTO authorDto = UserSummaryDTO.builder()
                .id(author != null ? author.getId() : null)
                .firstName(firstName)
                .lastName(lastName)
                .profileImageUrl(profileImageUrl)
                .build();

        return PostResponseDTO.builder()
                .id(post.getId())
                .author(authorDto)
                .visibility(post.getVisibility())
                .contentType(post.getContentType())
                .text(post.getText())
                .media(media)
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .myReaction(myReaction)
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
