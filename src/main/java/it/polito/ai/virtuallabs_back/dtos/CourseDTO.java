package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = false)
@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {

    @NotEmpty(message = "Name of the course must not be empty")
    private String name;
    @NotEmpty
    private String tag;
    @Min(1)
    @Max(40)
    private int min;
    @Min(1)
    @Max(40)
    private int max;
    private boolean enabled;
}
