package org.example.friendfinder.repository;

import org.example.friendfinder.model.Post;
import org.example.friendfinder.model.PostVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByDeletedFalseAndVisibilityAndAuthor_IdInOrderByCreatedAtDesc(
            PostVisibility visibility,
            Collection<Long> authorIds,
            Pageable pageable
    );

    Optional<Post> findByIdAndDeletedFalse(Long id);
}
