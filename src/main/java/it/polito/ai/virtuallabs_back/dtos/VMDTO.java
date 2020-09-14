package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = false)
@Data
public class VMDTO extends RepresentationModel<VMDTO> {

    private Long id;
    private String name;
    @NotNull(message = "Must not be null")
    private int vcpu;
    @NotNull(message = "Must not be null")
    private int disk;
    @NotNull(message = "Must not be null")
    private int ram;
    private boolean active;
}
