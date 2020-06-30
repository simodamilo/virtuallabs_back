package it.polito.ai.virtuallabs_back.exception;

public class SolutionChangeNotValid extends RuntimeException {
    public SolutionChangeNotValid(String msg) {
        super(msg);
    }
}
