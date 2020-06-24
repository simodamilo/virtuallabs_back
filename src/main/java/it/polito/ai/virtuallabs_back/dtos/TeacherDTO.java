package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class TeacherDTO extends RepresentationModel<TeacherDTO> {

    private String serial; // dxxxxxx
    private String email;
    private String name;
    private String surname;
    private Byte[] image;

}
