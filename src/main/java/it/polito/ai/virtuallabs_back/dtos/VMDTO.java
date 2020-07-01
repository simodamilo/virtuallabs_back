package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;

@Data
public class VMDTO extends RepresentationModel<VMDTO> {

    private Long id;
    @NotNull
    private int vcpu;
    @NotNull
    private int disk;
    @NotNull
    private int ram;
    //TODO non so se questo va messo non null o se lo settiamo di default o false, forse si può togliere completamente da qui
    private boolean active;

}
