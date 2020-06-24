package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class TeamDTO extends RepresentationModel<TeamDTO> {
    private Long id;
    private String name;
    private int status;
}
