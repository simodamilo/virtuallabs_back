package it.polito.ai.virtuallabs_back.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TeacherNotFoundException extends TeamServiceException {
    public TeacherNotFoundException(String msg) {
        super(msg);
    }
}
