package com.farmsense.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* -------- 404 -------- */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest req
    ) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(),ex.getMessage(), req);
    }

    /* -------- 401 -------- */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest req
    ) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getMessage(), req);
    }

    /* -------- 400 -------- */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest req
    ) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getMessage(),req);
    }

    /* -------- 500 (fallback) -------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest req
    ) {

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong : ",
                ex.getMessage(),
                req
        );
    }

    /* -------- BUILDER -------- */
    private ResponseEntity<ApiErrorResponse> buildError(
            HttpStatus status,
            String message, String errorDetail,
            HttpServletRequest req
    ) {
        return ResponseEntity.status(status).body(
                ApiErrorResponse.builder()
                        .success(false)
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .errorDetail(errorDetail)
                        .path(req.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /* -------- VALIDATION (400) -------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return buildError(HttpStatus.BAD_REQUEST, msg, ex.getMessage(),req);
    }
}
