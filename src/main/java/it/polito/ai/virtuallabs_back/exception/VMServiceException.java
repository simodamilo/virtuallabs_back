package it.polito.ai.virtuallabs_back.exception;

public class VMServiceException extends RuntimeException {
    public VMServiceException(String msg) {
        super(msg);
    }
}
