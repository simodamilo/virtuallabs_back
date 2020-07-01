package it.polito.ai.virtuallabs_back.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {

    @CsvBindByName
    @NotEmpty
    private String serial;
    @CsvBindByName
    @NotEmpty
    private String name;
    @CsvBindByName
    @NotEmpty
    private String surname;
    @NotEmpty
    private String email;
    private byte[] image;

}
