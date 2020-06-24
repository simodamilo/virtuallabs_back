package it.polito.ai.virtuallabs_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CourseSizeException extends TeamServiceException {
    public CourseSizeException(String msg) {
        super(msg);
    }
}
