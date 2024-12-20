package com.bhawesh_source.exception;

/**
 * Custom exception class for handling errors related to FlexiEnum operations.
 */
public class FlexiEnumException extends Exception {

    /**
     * Default constructor for FlexiEnumException.
     */
    public FlexiEnumException() {
        super();
    }

    /**
     * Constructor with a custom error message.
     *
     * @param message the custom error message.
     */
    public FlexiEnumException(String message) {
        super(message);
    }

    /**
     * Constructor with a custom error message and a cause.
     *
     * @param message the custom error message.
     * @param cause   the root cause of the exception.
     */
    public FlexiEnumException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a cause.
     *
     * @param cause the root cause of the exception.
     */
    public FlexiEnumException(Throwable cause) {
        super(cause);
    }
}

