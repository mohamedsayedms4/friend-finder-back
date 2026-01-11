package org.example.friendfinder.service;

import lombok.extern.slf4j.Slf4j;
import org.example.friendfinder.model.MediaType;
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

    // ---------- Allowed types ----------
    private static final Set<String> ALLOWED_IMAGE_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private static final Set<String> ALLOWED_VIDEO_TYPES =
            Set.of("video/mp4", "video/webm", "video/ogg", "video/quicktime");

    // ---------- Profile uploads ----------
    @Value("${app.uploads.profile-dir}")
    private String profileDir;

    // IMPORTANT: you set this to "assets/images" in env
    @Value("${app.uploads.profile-url-prefix:assets/images}")
    private String profileUrlPrefix;

    // ---------- Post media uploads ----------
    @Value("${app.uploads.post-dir:uploads/post-media}")
    private String postDir;

    // IMPORTANT: you set this to "assets/images" in env
    @Value("${app.uploads.post-url-prefix:assets/images}")
    private String postUrlPrefix;

    // ===================== PROFILE PICTURE =====================

    public String storeProfilePicture(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            log.debug("No profile picture provided (null/empty). userId={}", userId);
            return null;
        }

        String contentType = file.getContentType();
        long size = file.getSize();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            log.warn("Rejected profile upload: invalid content type. userId={}, contentType={}, size={}",
                    userId, contentType, size);
            throw new IllegalArgumentException("Invalid image type. Allowed: jpg, png, webp");
        }

        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "image" : file.getOriginalFilename()
        );
        String ext = getImageExtension(original, contentType);
        String filename = UUID.randomUUID() + ext;

        Path userFolder = Paths.get(profileDir, String.valueOf(userId));
        Path target = userFolder.resolve(filename);

        try {
            Files.createDirectories(userFolder);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // ✅ NO leading "/" anymore
            String storedWebPath = buildWebPath(profileUrlPrefix, userId, filename);

            log.info("Profile picture stored. userId={}, diskTarget={}, webPath={}",
                    userId, target.toAbsolutePath(), storedWebPath);

            return storedWebPath;

        } catch (IOException e) {
            log.error("Failed to store profile picture. userId={}, target={}",
                    userId, target.toAbsolutePath(), e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    // ===================== POST MEDIA (IMAGE/VIDEO) =====================

    /**
     * Stores a post media file (IMAGE/VIDEO) and returns its type + URL path for DB.
     */
    public StoredMedia storePostMedia(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }

        String contentType = file.getContentType();
        long size = file.getSize();

        if (contentType == null) {
            throw new IllegalArgumentException("Missing content type");
        }

        final MediaType mediaType;
        final String ext;

        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            mediaType = MediaType.IMAGE;
            String original = StringUtils.cleanPath(
                    file.getOriginalFilename() == null ? "image" : file.getOriginalFilename()
            );
            ext = getImageExtension(original, contentType);
        } else if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            mediaType = MediaType.VIDEO;
            String original = StringUtils.cleanPath(
                    file.getOriginalFilename() == null ? "video" : file.getOriginalFilename()
            );
            ext = getVideoExtension(original, contentType);
        } else {
            log.warn("Rejected post media upload: invalid content type. userId={}, contentType={}, size={}",
                    userId, contentType, size);
            throw new IllegalArgumentException(
                    "Invalid media type. Allowed images: jpg,png,webp. Allowed videos: mp4,webm,ogg,mov"
            );
        }

        String filename = UUID.randomUUID() + ext;

        Path userFolder = Paths.get(postDir, String.valueOf(userId));
        Path target = userFolder.resolve(filename);

        try {
            Files.createDirectories(userFolder);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // ✅ NO leading "/" anymore
            String storedWebPath = buildWebPath(postUrlPrefix, userId, filename);

            log.info("Post media stored. userId={}, type={}, diskTarget={}, webPath={}",
                    userId, mediaType, target.toAbsolutePath(), storedWebPath);

            return new StoredMedia(mediaType, storedWebPath);

        } catch (IOException e) {
            log.error("Failed to store post media. userId={}, target={}",
                    userId, target.toAbsolutePath(), e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    // ===================== HELPERS =====================

    /**
     * Builds a web path like:
     *   assets/images        -> assets/images/{userId}/{filename}
     *   uploads/post-media   -> uploads/post-media/{userId}/{filename}
     *
     * ✅ Does NOT add "/" at the beginning.
     */
    private String buildWebPath(String urlPrefix, Long userId, String filename) {
        String prefix = String.valueOf(urlPrefix)
                .replace("\\", "/")
                .trim();

        // remove leading slashes
        prefix = prefix.replaceAll("^/+", "");

        // remove trailing slashes
        prefix = prefix.replaceAll("/+$", "");

        return prefix + "/" + userId + "/" + filename;
    }

    private String getImageExtension(String originalFilename, String contentType) {
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

    private String getVideoExtension(String originalFilename, String contentType) {
        String lower = originalFilename.toLowerCase();
        if (lower.endsWith(".mp4")) return ".mp4";
        if (lower.endsWith(".webm")) return ".webm";
        if (lower.endsWith(".ogg") || lower.endsWith(".ogv")) return ".ogg";
        if (lower.endsWith(".mov")) return ".mov";

        return switch (contentType) {
            case "video/webm" -> ".webm";
            case "video/ogg" -> ".ogg";
            case "video/quicktime" -> ".mov";
            default -> ".mp4";
        };
    }

    /**
     * Result for post media storage.
     */
    public record StoredMedia(MediaType mediaType, String webPath) {}
}
