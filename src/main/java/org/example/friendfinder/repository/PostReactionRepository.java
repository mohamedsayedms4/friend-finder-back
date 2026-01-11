package org.example.friendfinder.repository;

import org.example.friendfinder.model.PostReaction;
import org.example.friendfinder.model.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Optional<PostReaction> findByPost_IdAndUser_Id(Long postId, Long userId);

    void deleteByPost_IdAndUser_Id(Long postId, Long userId);

    long countByPost_IdAndType(Long postId, ReactionType type);

    @Query("""
        select
          sum(case when r.type = 'LIKE' then 1 else 0 end),
          sum(case when r.type = 'DISLIKE' then 1 else 0 end)
        from PostReaction r
        where r.post.id = :postId
    """)
    Object[] countLikeDislike(Long postId);
}
