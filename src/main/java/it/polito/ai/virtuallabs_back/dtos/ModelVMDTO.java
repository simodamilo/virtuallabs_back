package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ModelVMDTO extends RepresentationModel<ModelVMDTO> {

    private Long id;
    private String name;
    private String type;
}
