package it.polito.ai.virtuallabs_back.dtos;

import it.polito.ai.virtuallabs_back.entities.Solution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = false)
@Data
public class SolutionDTO extends RepresentationModel<SolutionDTO> {

    private Long id;
    private byte[] content;
    @NotEmpty
    private Solution.State state;
    @NotEmpty
    private Timestamp deliveryTs;
    @NotNull
    private boolean modifiable;
}
