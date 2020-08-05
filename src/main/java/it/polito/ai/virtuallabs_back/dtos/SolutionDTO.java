package it.polito.ai.virtuallabs_back.dtos;

import it.polito.ai.virtuallabs_back.entities.Solution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = false)
@Data
public class SolutionDTO extends RepresentationModel<SolutionDTO> {

    private Long id;
    @NotBlank(message = "Must not be blank")
    private Solution.State state;
    @NotBlank(message = "Must not be blank")
    private Timestamp deliveryTs;
    private String grade;
    @NotBlank(message = "Must not be blank")
    private boolean modifiable;
}
