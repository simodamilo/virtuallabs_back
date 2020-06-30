package it.polito.ai.virtuallabs_back.exception;

public class CourseNotValidException extends RuntimeException {
    public CourseNotValidException(String msg) {
        super(msg);
    }
}