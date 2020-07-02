package it.polito.ai.virtuallabs_back.exception;

public class TeamTokenNotFoundException extends TeamServiceException {
    public TeamTokenNotFoundException(String msg) {
        super(msg);
    }
}
