package it.polito.ai.virtuallabs_back.exception;

public class TeamTokenExpiredException extends TeamServiceException {
    public TeamTokenExpiredException(String msg) {
        super(msg);
    }
}
