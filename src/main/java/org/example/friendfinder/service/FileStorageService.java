package org.example.friendfinder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${app.uploads.profile-dir}")
    private String profileDir;

    @Value("${app.uploads.profile-url-prefix:assets/images}")
    private String profileUrlPrefix;

    public String storeProfilePicture(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            log.debug("No profile picture provided (null/empty). userId={}", userId);
            return null;
        }

        String contentType = file.getContentType();
        long size = file.getSize();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("Rejected profile picture upload: invalid content type. userId={}, contentType={}, size={}",
                    userId, contentType, size);
            throw new IllegalArgumentException("Invalid image type. Allowed: jpg, png, webp");
        }

        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "image" : file.getOriginalFilename()
        );
        String ext = getExtension(original, contentType);
        String filename = UUID.randomUUID() + ext;

        Path userFolder = Paths.get(profileDir, String.valueOf(userId));
        Path target = userFolder.resolve(filename);

        try {
            Files.createDirectories(userFolder);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String prefix = profileUrlPrefix.replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
            String storedWebPath = prefix + "/" + userId + "/" + filename; // assets/images/{id}/{file}

            log.info("Profile picture stored. userId={}, diskTarget={}, webPath={}",
                    userId, target.toAbsolutePath(), storedWebPath);

            return storedWebPath;

        } catch (IOException e) {
            log.error("Failed to store profile picture. userId={}, target={}",
                    userId, target.toAbsolutePath(), e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private String getExtension(String originalFilename, String contentType) {
        String lower = originalFilename.toLowerCase();
        if (lower.endsWith(".png")) return ".png";
        if (lower.endsWith(".webp")) return ".webp";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return ".jpg";

        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
