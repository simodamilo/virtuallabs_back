package it.polito.ai.virtuallabs_back.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CourseChangeNotValidException extends TeamServiceException {
    public CourseChangeNotValidException(String msg) {
        super(msg);
    }
}
