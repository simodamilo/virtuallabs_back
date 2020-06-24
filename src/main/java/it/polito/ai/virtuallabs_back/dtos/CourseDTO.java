package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;


@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {

    @NotBlank(message = "name of the course must not be blank")
    private String name;
    private String tag;
    private int min;
    private int max;
    private boolean enabled;
}
