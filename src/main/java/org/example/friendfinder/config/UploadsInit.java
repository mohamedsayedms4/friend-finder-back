package org.example.friendfinder.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class UploadsInit {

    @Value("${app.uploads.profile-dir}")
    private String profileDir;

    @PostConstruct
    public void init() {
        try {
            Path dir = Paths.get(profileDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            log.info("Uploads directory ready: {}", dir);
        } catch (Exception e) {
            log.error("Failed to initialize uploads directory.", e);
            throw new IllegalStateException("Cannot initialize uploads directory", e);
        }
    }
}
