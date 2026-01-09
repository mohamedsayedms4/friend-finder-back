package org.example.friendfinder.common.exception;


/**
 * Thrown for invalid client requests.
 *
 * @author Mohamed Sayed
 */
public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message);
    }
}
