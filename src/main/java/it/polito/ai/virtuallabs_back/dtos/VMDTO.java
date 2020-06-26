package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class VMDTO extends RepresentationModel<VMDTO> {

    private Long id;
    private int vcpu;
    private int disk;
    private int ram;

}
