package com.ITQGroup.exception;

public class DocumentStatusConflictException extends DocumentProcessingException{

    public DocumentStatusConflictException(String message, String exceptionStatus) {
        super(message, exceptionStatus);
    }
}
