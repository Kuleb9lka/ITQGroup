package com.ITQGroup.exception;

public abstract class DocumentProcessingException extends RuntimeException{

    private String responseStatus;

    public DocumentProcessingException(String message) {
        super(message);
    }

    public DocumentProcessingException(String message, String responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }

    public String getResponseStatus() {
        return responseStatus;
    }
}
