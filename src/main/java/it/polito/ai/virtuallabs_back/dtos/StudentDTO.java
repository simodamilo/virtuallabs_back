package it.polito.ai.virtuallabs_back.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = false)
@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {

    @CsvBindByName
    @NotBlank(message = "Must not be blank")
    private String serial;
    @CsvBindByName
    @NotBlank(message = "Must not be blank")
    private String name;
    @CsvBindByName
    @NotBlank(message = "Must not be blank")
    private String surname;
    @NotBlank(message = "Must not be blank")
    private String email;
}
