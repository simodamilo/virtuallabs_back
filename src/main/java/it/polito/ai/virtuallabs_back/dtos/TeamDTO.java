package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@Data
public class TeamDTO extends RepresentationModel<TeamDTO> {

    private Long id;
    @NotEmpty
    private String name;
    private int status;
    private int vcpu;
    private int disk;
    private int ram;
    private int activeInstance;
    private int maxInstance;
}
