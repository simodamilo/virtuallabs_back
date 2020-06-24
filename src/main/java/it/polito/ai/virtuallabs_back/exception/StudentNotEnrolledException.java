package it.polito.ai.virtuallabs_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StudentNotEnrolledException extends TeamServiceException {
    public StudentNotEnrolledException(String msg) {
        super(msg);
    }
}
