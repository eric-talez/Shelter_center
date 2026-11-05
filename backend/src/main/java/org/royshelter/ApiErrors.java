package org.royshelter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(InvalidRequest.class)
    public ResponseEntity<Error> invalid(InvalidRequest exception) {
        return ResponseEntity.badRequest().body(new Error("INVALID_REQUEST", exception.getMessage(), null));
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<Error> notFound() {
        return ResponseEntity.status(404).body(new Error("NOT_FOUND", "동물을 찾을 수 없습니다.", null));
    }

    @ExceptionHandler(ExerciseNotImplementedException.class)
    public ResponseEntity<Error> unfinished(ExerciseNotImplementedException exception) {
        return ResponseEntity.status(501).body(new Error("EXERCISE_NOT_IMPLEMENTED", exception.getMessage(), exception.getLessonId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> unexpected(Exception exception) {
        return ResponseEntity.internalServerError().body(new Error("INTERNAL_ERROR", "요청을 처리하지 못했습니다.", null));
    }

    public record Error(String code, String message, String lessonId) { }
    static class InvalidRequest extends RuntimeException {
        InvalidRequest(String message) { super(message); }
    }
    static class NotFound extends RuntimeException { }
}
