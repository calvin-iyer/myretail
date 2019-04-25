package org.demo.myretail.service.exception;

import org.demo.myretail.controller.model.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

@Getter
public class ServiceResponseException extends Exception {

    private HttpStatus httpStatus;
    private String message;

    public ServiceResponseException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private ServiceResponseException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static ServiceResponseException unauthorized() {
        return new ServiceResponseException(HttpStatus.UNAUTHORIZED);
    }

    public static ServiceResponseException badRequest() {
        return new ServiceResponseException(HttpStatus.BAD_REQUEST);
    }

    public static ServiceResponseException internalServerError() {
        return new ServiceResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ServiceResponseException message(String message) {
        this.message = message;
        return this;
    }
    
    public ResponseEntity getResponseEntity() {
      return ResponseEntity.status(getHttpStatus()).body(new MessageResponse(getMessage()));
    }
    
    public ResponseEntity getResponseEntity(String customMessage) {
      return ResponseEntity.status(getHttpStatus()).body(new MessageResponse(customMessage));
    }
    
    public ResponseEntity getResponseEntity(HttpStatus customStatus) {
      return ResponseEntity.status(customStatus).body(new MessageResponse(getMessage()));
    }
}

