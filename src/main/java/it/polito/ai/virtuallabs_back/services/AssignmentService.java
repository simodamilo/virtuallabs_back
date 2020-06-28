package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    /**
     *
     */
    Optional<AssignmentDTO> getAssignment(Long id);

    /**
     *
     */
    List<AssignmentDTO> getAssignmentsByCourse(String courseName);

    /**
     *
     */
    @PreAuthorize("hasRole('TEACHER')")
    boolean addAssignment(AssignmentDTO assignmentDTO);

    /**
     *
     */
    @PreAuthorize("hasRole('TEACHER')")
    boolean modifyAssignment(AssignmentDTO assignmentDTO);
}
