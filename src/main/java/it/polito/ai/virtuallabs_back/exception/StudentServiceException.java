package it.polito.ai.virtuallabs_back.exception;

public class StudentServiceException extends RuntimeException {
    public StudentServiceException(String msg) {
        super(msg);
    }
}
