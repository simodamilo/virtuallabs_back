package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    /**
     * @param id
     * @return
     */
    Optional<AssignmentDTO> getAssignment(Long id);

    /**
     *
     * @param courseName
     * @return
     */
    List<AssignmentDTO> getAssignmentsByCourse(String courseName);

    /**
     *
     * @param assignmentDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, String courseName);

    /**
     * @param assignmentDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    AssignmentDTO modifyAssignment(AssignmentDTO assignmentDTO);

    /**
     *
     */
    void assignmentExpired();

}
