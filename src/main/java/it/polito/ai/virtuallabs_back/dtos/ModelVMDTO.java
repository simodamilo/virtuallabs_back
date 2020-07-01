package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@Data
public class ModelVMDTO extends RepresentationModel<ModelVMDTO> {

    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String type;
}
