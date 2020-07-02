package it.polito.ai.virtuallabs_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ModelVMNotFoundException extends ModelVMServiceException {
    public ModelVMNotFoundException(String msg) {
        super(msg);
    }
}
