package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;
    @NotBlank(message = "Must not be blank")
    private String username;
    @NotBlank(message = "Must not be blank")
    private String password;
}