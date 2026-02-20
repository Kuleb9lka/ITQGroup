package com.ITQGroup.exception;

public class DocumentNotFoundException extends DocumentProcessingException{

    public DocumentNotFoundException(String message, String exceptionStatus) {
        super(message, exceptionStatus);
    }
}
