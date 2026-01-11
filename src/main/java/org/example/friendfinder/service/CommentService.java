package org.example.friendfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.CommentResponseDTO;
import org.example.friendfinder.dto.CreateCommentRequest;
import org.example.friendfinder.dto.UserSummaryDTO;
import org.example.friendfinder.model.Post;
import org.example.friendfinder.model.PostComment;
import org.example.friendfinder.model.User;
import org.example.friendfinder.model.UserProfile;
import org.example.friendfinder.repository.PostCommentRepository;
import org.example.friendfinder.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostAccessGuard guard;

    @Transactional
    public CommentResponseDTO add(Long meId, Long postId, CreateCommentRequest req) {
        Post post = guard.mustGetVisiblePost(meId, postId);

        PostComment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent comment not found"));

            if (!parent.getPost().getId().equals(postId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent comment belongs to a different post");
            }
        }

        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        PostComment c = PostComment.builder()
                .post(post)
                .author(me)
                .parent(parent)
                .content(req.getContent())
                .deleted(false)
                .build();

        return toDto(commentRepository.save(c));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> listTopLevel(Long meId, Long postId, Pageable pageable) {
        guard.mustGetVisiblePost(meId, postId);
        return commentRepository
                .findByPost_IdAndParentIsNullOrderByCreatedAtAsc(postId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> listReplies(Long meId, Long postId, Long parentId, Pageable pageable) {
        guard.mustGetVisiblePost(meId, postId);
        return commentRepository
                .findByPost_IdAndParent_IdOrderByCreatedAtAsc(postId, parentId, pageable)
                .map(this::toDto);
    }

    @Transactional
    public void delete(Long meId, Long commentId) {
        PostComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        guard.mustGetVisiblePost(meId, c.getPost().getId());

        if (!c.getAuthor().getId().equals(meId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this comment");
        }

        c.softDelete();
        commentRepository.save(c);
    }

    // ===================== MAPPER =====================

    private CommentResponseDTO toDto(PostComment c) {
        User author = c.getAuthor();
        UserProfile profile = author.getProfile();

        return CommentResponseDTO.builder()
                .id(c.getId())
                .postId(c.getPost().getId())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .author(UserSummaryDTO.builder()
                        .id(author.getId())
                        .firstName(profile != null ? profile.getFirstName() : null)
                        .lastName(profile != null ? profile.getLastName() : null)
                        .profileImageUrl(profile != null ? profile.getProfilePicture() : null)
                        .build())
                .content(c.getContent())
                .deleted(c.isDeleted())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
