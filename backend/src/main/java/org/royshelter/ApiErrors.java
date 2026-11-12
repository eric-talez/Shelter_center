package org.royshelter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiErrors {
    private static final Logger logger = LoggerFactory.getLogger(ApiErrors.class);

    @ExceptionHandler(InvalidRequest.class)
    public ResponseEntity<Error> invalid(InvalidRequest exception) {
        return ResponseEntity.badRequest().body(new Error("INVALID_REQUEST", exception.getMessage(), null));
    }

    @ExceptionHandler({NotFound.class, NoResourceFoundException.class})
    public ResponseEntity<Error> notFound() {
        return ResponseEntity.status(404).body(new Error("NOT_FOUND", "요청한 정보를 찾을 수 없습니다.", null));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Error> methodNotAllowed(HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity.status(405).headers(exception.getHeaders())
                .body(new Error("METHOD_NOT_ALLOWED", "지원하지 않는 요청 방식입니다.", null));
    }

    @ExceptionHandler(ExerciseNotImplementedException.class)
    public ResponseEntity<Error> unfinished(ExerciseNotImplementedException exception) {
        return ResponseEntity.status(501).body(new Error("EXERCISE_NOT_IMPLEMENTED", exception.getMessage(), exception.getLessonId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> unexpected(Exception exception) {
        logger.error("Unexpected request failure", exception);
        return ResponseEntity.internalServerError().body(new Error("INTERNAL_ERROR", "요청을 처리하지 못했습니다.", null));
    }

    public record Error(String code, String message, String lessonId) { }
    static class InvalidRequest extends RuntimeException {
        InvalidRequest(String message) { super(message); }
    }
    static class NotFound extends RuntimeException { }
}
