package it.polito.ai.virtuallabs_back.dtos;

import it.polito.ai.virtuallabs_back.entities.Solution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = false)
@Data
public class SolutionDTO extends RepresentationModel<SolutionDTO> {

    private Long id;
    @NotNull(message = "Must not be null")
    private Solution.State state;
    @NotNull(message = "Must not be null")
    private Timestamp deliveryTs;
    private String grade;
    @NotNull(message = "Must not be null")
    private boolean modifiable;
}
