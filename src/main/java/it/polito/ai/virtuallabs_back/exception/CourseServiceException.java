package it.polito.ai.virtuallabs_back.exception;

public class CourseServiceException extends RuntimeException {
    public CourseServiceException(String msg) {
        super(msg);
    }
}