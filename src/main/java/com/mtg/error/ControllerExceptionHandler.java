package com.mtg.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> DataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        String excMessage = e.getMessage();
        if (excMessage.toLowerCase().contains("unique")) {
            excMessage = "Unique constraint violation for field";
            if (e.getMessage().contains("IX_NAME")) {
                excMessage += " 'name'";
            }
        }
        ErrorMessage message = new ErrorMessage(new Date(), HttpStatus.CONFLICT.value(), excMessage, request.getRequestURI());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> EntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        ErrorMessage message = new ErrorMessage(new Date(), HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> HttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        ErrorMessage message = new ErrorMessage(new Date(), HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidCardTypeException.class)
    public ResponseEntity<ErrorMessage> InvalidCardTypeException(InvalidCardTypeException e, HttpServletRequest request) {
        ErrorMessage message = new ErrorMessage(new Date(), HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> IllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        ErrorMessage message = new ErrorMessage(new Date(), HttpStatus.CONFLICT.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> MethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        System.out.println(e.getFieldError().getDefaultMessage());
        String errorMessage = String.valueOf(e.getFieldError());
        if (e.getFieldError().getDefaultMessage() != null) {
            errorMessage = e.getFieldError().getDefaultMessage();
        }
        ErrorMessage message = new ErrorMessage(new Date(), HttpStatus.BAD_REQUEST.value(), errorMessage, request.getRequestURI());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecordAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> RecordAlreadyExistsException(RecordAlreadyExistsException e, HttpServletRequest request) {
        ErrorMessage message = new ErrorMessage(new Date(), HttpStatus.CONFLICT.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorMessage> handleTokenRefreshException(TokenRefreshException ex, HttpServletRequest request) {
        ErrorMessage message = new ErrorMessage(
                new Date(),
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

}
