package it.polito.ai.virtuallabs_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ModelVMChangeNotValidException extends ModelVMServiceException {
    public ModelVMChangeNotValidException(String msg) {
        super(msg);
    }
}
