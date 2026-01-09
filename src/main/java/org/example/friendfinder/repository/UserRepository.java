package org.example.friendfinder.repository;


import org.example.friendfinder.dto.UserSuggestionDTO;
import org.example.friendfinder.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


/**
 * User persistence operations.
 *
 * @author Mohamed Sayed
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * @param email user email
     * @return optional user
     */
    @EntityGraph(attributePaths = {"role"})

    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user exists with the given email.
     *
     * @param email user email
     * @return true if exists; otherwise false
     */
    boolean existsByEmail(String email);



    @Query("""
        select new org.example.friendfinder.dto.UserSuggestionDTO(
            u.id,
            u.email,
            p.firstName,
            p.lastName,
            p.profilePicture
        )
        from User u
        left join UserProfile p on p.user.id = u.id
        where u.email <> :myEmail
        order by u.id desc
    """)
    List<UserSuggestionDTO> findSuggestions(@Param("myEmail") String myEmail, Pageable pageable);
}
