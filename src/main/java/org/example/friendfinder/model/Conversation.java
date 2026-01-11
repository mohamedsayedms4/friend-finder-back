package org.example.friendfinder.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "conversations",
        uniqueConstraints = @UniqueConstraint(name="uk_conversation_pair", columnNames={"user1_id","user2_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user1_id", nullable=false, foreignKey=@ForeignKey(name="fk_conv_user1"))
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user2_id", nullable=false, foreignKey=@ForeignKey(name="fk_conv_user2"))
    private User user2;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
