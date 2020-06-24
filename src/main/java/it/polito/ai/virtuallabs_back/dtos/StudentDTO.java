package it.polito.ai.virtuallabs_back.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {

    private String serial;
    @CsvBindByName
    @NotBlank
    private String name;
    @CsvBindByName
    @NotBlank
    private String surname;
    private String email;
    private Byte[] image;

}
