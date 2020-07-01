package it.polito.ai.virtuallabs_back.exception;

public class TeamTokenExpiredException extends RuntimeException {
    public TeamTokenExpiredException(String msg) {
        super(msg);
    }
}
