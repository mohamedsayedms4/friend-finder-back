package org.example.friendfinder.repository;

import org.example.friendfinder.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
        select c from Conversation c
        where (c.user1.id = :u1 and c.user2.id = :u2)
           or (c.user1.id = :u2 and c.user2.id = :u1)
    """)
    Optional<Conversation> findBetweenUsers(Long u1, Long u2);



}
