package org.example.friendfinder.config;


import org.example.friendfinder.model.Role;
import org.example.friendfinder.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Seeds default roles.
 *
 * @author Mohamed Sayed
 */
@Configuration
public class RoleSeeder {

    @Bean
    CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            List<String> roles = List.of("USER", "ADMIN");
            for (String r : roles) {
                if (!roleRepository.existsByName(r)) {
                    roleRepository.save(Role.builder().name(r).build());
                }
            }
        };
    }
}
