package it.polito.ai.virtuallabs_back.exception;

public class TeamServiceException extends RuntimeException {
    public TeamServiceException(String msg) {
        super(msg);
    }
}
