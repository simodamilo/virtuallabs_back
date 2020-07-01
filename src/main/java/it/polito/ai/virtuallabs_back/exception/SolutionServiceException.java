package it.polito.ai.virtuallabs_back.exception;

public class SolutionServiceException extends RuntimeException {
    public SolutionServiceException(String msg) {
        super(msg);
    }
}
