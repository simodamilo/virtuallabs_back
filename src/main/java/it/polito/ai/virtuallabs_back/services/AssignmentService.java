package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    Optional<AssignmentDTO> getAssignment(Long id);

    List<AssignmentDTO> getAssignmentsByCourse(String courseName);

    boolean addAssignment(AssignmentDTO assignmentDTO);
}
