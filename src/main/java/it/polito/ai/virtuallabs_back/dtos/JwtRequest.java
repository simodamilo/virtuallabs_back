package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}