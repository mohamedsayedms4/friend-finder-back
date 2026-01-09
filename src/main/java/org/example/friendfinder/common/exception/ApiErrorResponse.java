package org.example.friendfinder.common.exception;


import lombok.*;

import java.time.Instant;

/**
 * Standard error response body.
 *
 * @author Mohamed Sayed
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private Instant timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;
}
