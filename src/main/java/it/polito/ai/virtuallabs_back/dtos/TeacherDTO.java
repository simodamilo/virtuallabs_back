package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = false)
@Data
public class TeacherDTO extends RepresentationModel<TeacherDTO> {

    @NotBlank(message = "Must not be blank")
    private String serial;
    @NotBlank(message = "Must not be blank")
    private String email;
    @NotBlank(message = "Must not be blank")
    private String name;
    @NotBlank(message = "Must not be blank")
    private String surname;
}
