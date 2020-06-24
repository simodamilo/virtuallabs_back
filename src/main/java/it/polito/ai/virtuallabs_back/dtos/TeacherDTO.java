package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@Data
public class TeacherDTO extends RepresentationModel<TeacherDTO> {

    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String firstName;
}
