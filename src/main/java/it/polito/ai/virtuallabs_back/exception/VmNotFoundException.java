package it.polito.ai.virtuallabs_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class VmNotFoundException extends VMServiceException {
    public VmNotFoundException(String msg) {
        super(msg);
    }
}
