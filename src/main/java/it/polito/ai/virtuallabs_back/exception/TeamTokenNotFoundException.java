package it.polito.ai.virtuallabs_back.exception;

public class TeamTokenNotFoundException extends RuntimeException {
    public TeamTokenNotFoundException(String msg) {
        super(msg);
    }
}
