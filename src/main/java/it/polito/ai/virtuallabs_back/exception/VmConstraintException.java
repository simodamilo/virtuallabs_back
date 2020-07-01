package it.polito.ai.virtuallabs_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class VmConstraintException extends TeamServiceException {
    public VmConstraintException(String msg) {
        super(msg);
    }
}
