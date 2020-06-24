package it.polito.ai.virtuallabs_back.dtos;

import org.springframework.hateoas.RepresentationModel;

public class ModelVMDTO extends RepresentationModel<ModelVMDTO> {

    private String id;
    private String name;
}
