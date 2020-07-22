package it.polito.ai.virtuallabs_back.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = false)
@Data
public class TeamTokenDTO extends RepresentationModel<TeamTokenDTO> {

    @NotEmpty
    private String id;
    private Long teamId;
    private String studentSerial;
    private Timestamp expiryDate;
    private int status;
}
