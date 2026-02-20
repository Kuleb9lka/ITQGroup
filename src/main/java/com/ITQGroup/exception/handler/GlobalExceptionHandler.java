package com.ITQGroup.exception.handler;

import com.ITQGroup.dto.ExceptionResponseDto;
import com.ITQGroup.exception.ApprovalRegistryException;
import com.ITQGroup.exception.DocumentNotFoundException;
import com.ITQGroup.exception.DocumentStatusConflictException;
import com.ITQGroup.exception.StatusNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApprovalRegistryException.class)
    public ResponseEntity<ExceptionResponseDto> handleApprovalRegistryException(ApprovalRegistryException exception) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponseDto(exception.getResponseStatus(), exception.getMessage()));
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleDocumentNotFoundException(DocumentNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponseDto(exception.getResponseStatus(), exception.getMessage()));
    }

    @ExceptionHandler(DocumentStatusConflictException.class)
    public ResponseEntity<ExceptionResponseDto> handleDocumentStatusConflictException(DocumentStatusConflictException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponseDto(exception.getResponseStatus(), exception.getMessage()));
    }

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleStatusNotFoundException(StatusNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponseDto(exception.getResponseStatus(), exception.getMessage()));
    }
}
