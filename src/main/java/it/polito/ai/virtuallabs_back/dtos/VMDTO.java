package it.polito.ai.virtuallabs_back.dtos;

import org.springframework.hateoas.RepresentationModel;

public class VMDTO extends RepresentationModel<VMDTO> {

    private Long id;
    private int vcpu;
    private int disk;
    private int ram;

}
