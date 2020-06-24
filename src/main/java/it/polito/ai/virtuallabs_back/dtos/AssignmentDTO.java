package it.polito.ai.virtuallabs_back.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

public class AssignmentDTO extends RepresentationModel<AssignmentDTO> {

    private Long id;
    private Date releaseDate;
    private Date deadline;
    private Byte[] content;
}
