//// src/main/java/org/example/friendfinder/config/UploadsWebConfig.java
//package org.example.friendfinder.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.nio.file.Path;
//
//@Configuration
//public class UploadsWebConfig implements WebMvcConfigurer {
//
//    @Value("${app.uploads.profile-dir}")
//    private String profileDir;
//
//    @Value("${app.uploads.profile-url-prefix:/uploads/profile-pictures}")
//    private String profileUrlPrefix;
//
//    @Value("${app.uploads.post-dir}")
//    private String postDir;
//
//    @Value("${app.uploads.post-url-prefix:/uploads/post-media}")
//    private String postUrlPrefix;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // profile pictures
//        registry.addResourceHandler(normalize(pattern(profileUrlPrefix)))
//                .addResourceLocations(toLocation(profileDir));
//
//        // post media
//        registry.addResourceHandler(normalize(pattern(postUrlPrefix)))
//                .addResourceLocations(toLocation(postDir));
//    }
//
//    private String pattern(String prefix) {
//        String p = prefix.replace("\\", "/").trim();
//        if (!p.startsWith("/")) p = "/" + p;
//        while (p.endsWith("/")) p = p.substring(0, p.length() - 1);
//        return p + "/**";
//    }
//
//    private String normalize(String s) {
//        return s.replaceAll("//+", "/");
//    }
//
//    private String toLocation(String dir) {
//        Path path = Path.of(dir).toAbsolutePath().normalize();
//        return path.toUri().toString(); // "file:/C:/.../"
//    }
//}
