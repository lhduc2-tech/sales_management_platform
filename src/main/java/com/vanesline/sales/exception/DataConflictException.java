package com.vanesline.sales.exception;

public class DataConflictException extends RuntimeException {

    public DataConflictException() {
        super();
    }

    public DataConflictException(String message) {
        super(message);
    }

    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
