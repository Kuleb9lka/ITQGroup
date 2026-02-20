package com.ITQGroup.exception;

public class StatusNotFoundException extends DocumentProcessingException{

    public StatusNotFoundException(String message, String exceptionStatus) {
        super(message, exceptionStatus);
    }
}
