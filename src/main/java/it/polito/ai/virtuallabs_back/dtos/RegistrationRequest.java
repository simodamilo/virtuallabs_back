package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Must not be blank")
    private String name;
    @NotBlank(message = "Must not be blank")
    private String surname;
    @NotBlank(message = "Must not be blank")
    private String serial;
    @NotBlank(message = "Must not be blank")
    private String email;
    @NotBlank(message = "Must not be blank")
    private String password;
}
