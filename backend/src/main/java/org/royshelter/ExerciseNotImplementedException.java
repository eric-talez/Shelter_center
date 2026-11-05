package org.royshelter;
public class ExerciseNotImplementedException extends RuntimeException {
    private final String lessonId;
    public ExerciseNotImplementedException(String lessonId) {
        super("아직 구현하지 않은 학습 기능입니다. (" + lessonId + ")");
        this.lessonId = lessonId;
    }
    public String getLessonId() { return lessonId; }
}
