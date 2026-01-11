package org.example.friendfinder.repository;

import org.example.friendfinder.model.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    Page<PostComment> findByPost_IdAndParentIsNullOrderByCreatedAtAsc(Long postId, Pageable pageable);

    Page<PostComment> findByPost_IdAndParent_IdOrderByCreatedAtAsc(Long postId, Long parentId, Pageable pageable);

    long countByPost_Id(Long postId);

    Optional<PostComment> findById(Long id);
}
