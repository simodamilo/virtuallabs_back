package it.polito.ai.virtuallabs_back.dtos;

import it.polito.ai.virtuallabs_back.entities.Solution;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class SolutionDTO extends RepresentationModel<SolutionDTO> {

    private Long id;
    @NotNull
    private Byte[] content;
    @NotNull
    private Solution.State state;
    @NotNull
    private Timestamp deliveryTs;
    @NotNull
    private boolean active;

}
