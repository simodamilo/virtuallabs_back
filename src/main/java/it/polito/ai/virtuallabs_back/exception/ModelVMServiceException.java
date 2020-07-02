package it.polito.ai.virtuallabs_back.exception;

public class ModelVMServiceException extends RuntimeException {
    public ModelVMServiceException(String msg) {
        super(msg);
    }
}
