package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;
    private String username;
    private String password;
}