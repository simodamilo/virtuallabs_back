package it.polito.ai.virtuallabs_back.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TeamNotFoundException extends TeamServiceException {
    public TeamNotFoundException(String msg) {
        super(msg);
    }
}
