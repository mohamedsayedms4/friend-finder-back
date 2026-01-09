package org.example.friendfinder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM
        ));
        converters.add(0, converter);
    }

//    @Value("${app.uploads.profile-dir}")
//    private String profileDir;
//
//    @Value("${app.uploads.profile-url-prefix:/uploads/profile-pictures}")
//    private String urlPrefix;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String location = "file:" + profileDir + "/";
//
//        registry.addResourceHandler(urlPrefix + "/**")
//                .addResourceLocations(location);
//    }
}