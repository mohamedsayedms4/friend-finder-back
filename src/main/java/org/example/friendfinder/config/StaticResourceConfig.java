//package org.example.friendfinder.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.*;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Configuration
//public class StaticResourceConfig implements WebMvcConfigurer {
//
//    @Value("${app.uploads.profile-dir}")
//    private String profileDir;
//
//    @Value("${app.uploads.profile-url-prefix:/uploads/profile-pictures}")
//    private String profileUrlPrefix;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        Path dir = Paths.get(profileDir).toAbsolutePath().normalize();
//        String location = dir.toUri().toString(); // file:/C:/.../
//
//        String prefix = profileUrlPrefix.startsWith("/") ? profileUrlPrefix : ("/" + profileUrlPrefix);
//        String pattern = prefix.endsWith("/") ? (prefix + "**") : (prefix + "/**");
//
//        registry.addResourceHandler(pattern)
//                .addResourceLocations(location)
//                .setCachePeriod(3600);
//    }
//}
