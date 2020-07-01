package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RegistrationRequest {

    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    @NotEmpty
    private String serial;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
