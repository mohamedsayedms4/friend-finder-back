package org.example.friendfinder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Upload properties binding.
 *
 * @author Mohamed Sayed
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.uploads")
public class UploadProperties {

    /**
     * Base directory for profile pictures.
     * Example: uploads/profile-pictures
     */
    private String profileDir;
}
