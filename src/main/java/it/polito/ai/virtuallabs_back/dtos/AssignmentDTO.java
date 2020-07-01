package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class AssignmentDTO extends RepresentationModel<AssignmentDTO> {

    private Long id;
    @NotEmpty
    private Date releaseDate;
    @NotEmpty
    private Date deadline;
    private byte[] content;
}
