package org.example.friendfinder.common.api;


import lombok.*;

import java.time.Instant;

/**
 * Standard API response envelope.
 *
 * @param <T> payload type
 * @author Mohamed Sayed
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Instant timestamp;
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(Instant.now())
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> fail(String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(Instant.now())
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}
