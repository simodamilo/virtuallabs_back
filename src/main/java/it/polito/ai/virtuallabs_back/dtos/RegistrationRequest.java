package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegistrationRequest {

    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private String serial;
    @NotNull
    private String email;
    @NotNull
    private String password;
}
