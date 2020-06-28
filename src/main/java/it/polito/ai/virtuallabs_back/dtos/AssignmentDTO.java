package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class AssignmentDTO extends RepresentationModel<AssignmentDTO> {

    private Long id;
    @NotNull
    private Date releaseDate;
    @NotNull
    private Date deadline;
    @NotNull
    private Byte[] content;
}
