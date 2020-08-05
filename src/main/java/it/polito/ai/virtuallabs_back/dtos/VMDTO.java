package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = false)
@Data
public class VMDTO extends RepresentationModel<VMDTO> {

    private Long id;
    @NotBlank(message = "Must not be blank")
    private String name;
    @NotBlank(message = "Must not be blank")
    private int vcpu;
    @NotBlank(message = "Must not be blank")
    private int disk;
    @NotBlank(message = "Must not be blank")
    private int ram;
    private boolean active;
}
