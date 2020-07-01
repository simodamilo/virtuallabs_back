package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@Data
public class VMDTO extends RepresentationModel<VMDTO> {

    private Long id;
    @NotEmpty
    private int vcpu;
    @NotEmpty
    private int disk;
    @NotEmpty
    private int ram;
    private boolean active;

}
