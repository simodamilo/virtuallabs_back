package it.polito.ai.virtuallabs_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StudentAlreadyInCourseException extends StudentServiceException {
    public StudentAlreadyInCourseException(String msg) {
        super(msg);
    }
}
