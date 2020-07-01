package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@Data
public class TeacherDTO extends RepresentationModel<TeacherDTO> {

    @NotEmpty
    private String serial;
    @NotEmpty
    private String email;
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    private Byte[] image;

}
