package org.example.friendfinder.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_media",
        indexes = {
                @Index(name = "idx_post_media_post", columnList = "post_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private MediaType mediaType;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private int position;
}
