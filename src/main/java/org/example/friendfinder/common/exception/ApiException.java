package org.example.friendfinder.common.exception;


/**
 * Base API exception.
 *
 * @author Mohamed Sayed
 */
public abstract class ApiException extends RuntimeException {

    protected ApiException(String message) {
        super(message);
    }
}
