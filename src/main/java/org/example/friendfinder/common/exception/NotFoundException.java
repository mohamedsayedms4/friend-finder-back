package org.example.friendfinder.common.exception;


/**
 * Thrown when a resource cannot be found.
 *
 * @author Mohamed Sayed
 */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message);
    }
}
