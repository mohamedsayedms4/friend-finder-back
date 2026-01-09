package org.example.friendfinder.common.exception;


/**
 * Thrown when authentication/authorization fails.
 *
 * @author Mohamed Sayed
 */
public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
