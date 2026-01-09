package org.example.friendfinder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.friendfinder.dto.RegisterRequest;
import org.springframework.http.*;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RegisterRequestPartConverter extends AbstractHttpMessageConverter<RegisterRequest> {

    private final ObjectMapper objectMapper;

    public RegisterRequestPartConverter(ObjectMapper objectMapper) {
        super(
                MediaType.APPLICATION_JSON,
                new MediaType("text", "plain", StandardCharsets.UTF_8)
        );
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return RegisterRequest.class.isAssignableFrom(clazz);
    }

    @Override
    protected RegisterRequest readInternal(Class<? extends RegisterRequest> clazz,
                                           HttpInputMessage inputMessage) throws IOException {
        String raw = new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8).trim();
        return objectMapper.readValue(raw, RegisterRequest.class);
    }

    @Override
    protected void writeInternal(RegisterRequest registerRequest,
                                 HttpOutputMessage outputMessage) throws IOException {
        byte[] out = objectMapper.writeValueAsBytes(registerRequest);
        outputMessage.getBody().write(out);
    }
}
