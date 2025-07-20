package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        List<String> errors = new ArrayList<>();
        errors.add("Not found: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        errors.add("Validation exception: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        List<String> errors = new ArrayList<>();
        errors.add("Access denied: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        List<String> errors = new ArrayList<>();
        errors.add("Conflict: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        List<String> errors = new ArrayList<>();
        errors.add("Bad request: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
        List<String> errors = new ArrayList<>();
        errors.add("Server error: " + e.getMessage());
        return createErrorResponse(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(List<String> errors, HttpStatus status) {
        ErrorResponse response = new ErrorResponse(errors, LocalDateTime.now(), status.value());
        return new ResponseEntity<>(response, status);
    }
}