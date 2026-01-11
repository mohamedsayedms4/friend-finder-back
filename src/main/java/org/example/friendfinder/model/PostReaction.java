package org.example.friendfinder.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "post_reactions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_post_user_reaction", columnNames = {"post_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_reactions_post", columnList = "post_id"),
                @Index(name = "idx_reactions_user", columnList = "user_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PostReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReactionType type;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
