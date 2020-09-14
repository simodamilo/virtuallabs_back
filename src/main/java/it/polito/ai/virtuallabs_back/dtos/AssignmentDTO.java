package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
public class AssignmentDTO extends RepresentationModel<AssignmentDTO> {

    private Long id;
    @NotBlank(message = "Must not be blank")
    private String name;
    @NotNull(message = "Must not be null")
    private Date releaseDate;
    @NotNull(message = "Must not be null")
    private Date deadline;
}
