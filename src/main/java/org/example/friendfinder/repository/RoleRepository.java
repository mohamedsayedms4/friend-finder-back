package org.example.friendfinder.repository;

import org.example.friendfinder.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Role persistence operations.
 *
 * @author Mohamed Sayed
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
