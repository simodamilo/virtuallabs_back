package it.polito.ai.virtuallabs_back.exception;

public class SolutionNotFoundException extends RuntimeException {
    public SolutionNotFoundException(String msg) {
        super(msg);
    }
}
