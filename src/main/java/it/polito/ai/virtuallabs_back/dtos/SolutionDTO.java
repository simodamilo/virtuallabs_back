package it.polito.ai.virtuallabs_back.dtos;

import it.polito.ai.virtuallabs_back.entities.Solution;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

public class SolutionDTO extends RepresentationModel<SolutionDTO> {

    private Long id;
    private Byte[] content;
    private Solution.State state;
    private Timestamp deliveryTs;
    private boolean active;

}
