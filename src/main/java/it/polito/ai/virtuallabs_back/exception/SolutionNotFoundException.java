package it.polito.ai.virtuallabs_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SolutionNotFoundException extends SolutionServiceException {
    public SolutionNotFoundException(String msg) {
        super(msg);
    }
}
