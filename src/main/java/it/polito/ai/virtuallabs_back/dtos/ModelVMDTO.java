package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ModelVMDTO extends RepresentationModel<ModelVMDTO> {

    private String id;
    private String name;
}
