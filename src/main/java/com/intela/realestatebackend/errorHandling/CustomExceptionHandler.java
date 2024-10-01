package com.intela.realestatebackend.errorHandling;

import com.intela.realestatebackend.exceptions.MissingAccessTokenException;
import com.intela.realestatebackend.exceptions.MissingRefreshTokenException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(MissingAccessTokenException.class)
    public ResponseEntity<String> onMissingAccessTokenException(MissingAccessTokenException exception) {
        return ResponseEntity.status(403).body("Missing access token" + exception.getMessage());
    }

    @ExceptionHandler(MissingRefreshTokenException.class)
    public ResponseEntity<String> onMissingRefreshTokenException(MissingRefreshTokenException exception) {
        return ResponseEntity.status(403).body("Missing refresh token" + exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail onNotFoundException(EntityNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail onRuntimeException(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail onUsernameNotFoundException(UsernameNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
