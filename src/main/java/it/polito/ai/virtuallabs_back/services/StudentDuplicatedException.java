package it.polito.ai.virtuallabs_back.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StudentDuplicatedException extends TeamServiceException {
    public StudentDuplicatedException(String msg) {
        super(msg);
    }
}
