package org.example.friendfinder.common.exception;


/**
 * Thrown when a request conflicts with current state (e.g., duplicate email).
 *
 * @author Mohamed Sayed
 */
public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message);
    }
}
