package org.example.friendfinder.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.friendfinder.model.User;

/**
 * UserProfile entity (One-to-One with User using shared primary key).
 *
 * @author Mohamed Sayed
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private Long id; // نفس ID بتاع User

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_profiles_users"))
    private User user;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 1000)
    private String biography;

    @Column(length = 500)
    private String profilePicture; // path or URL

    @Column(nullable = false, length = 30)
    private String phoneNumber;
}
