package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = false)
@Data
public class TeamDTO extends RepresentationModel<TeamDTO> {

    private Long id;
    @NotBlank(message = "Must not be blank")
    private String name;
    private int duration;
    private int status;
    private int vcpu;
    private int disk;
    private int ram;
    private int activeInstance;
    private int maxInstance;
}
