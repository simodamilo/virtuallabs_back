package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    /**
     * Used to get an assignment by the Id.
     *
     * @param id of the desired assignment.
     * @return empty optional if the assignment misses.
     */
    Optional<AssignmentDTO> getAssignment(Long id);

    /**
     * Used to get the list of assignments of a course.
     *
     * @param courseName in which assignments are searched.
     * @return list of found assignments.
     */
    List<AssignmentDTO> getCourseAssignments(String courseName);

    /**
     * Used by the teacher to add an assignment to the course.
     * @param assignmentDTO which needs to be added.
     * @return the added assignment.
     */
    @PreAuthorize("hasRole('TEACHER')")
    AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, String courseName);

    /**
     * Used by the teacher to modify an assignment.
     * @param assignmentDTO which needs to be modified.
     * @return the modified assignment.
     */
    @PreAuthorize("hasRole('TEACHER')")
    AssignmentDTO modifyAssignment(AssignmentDTO assignmentDTO);

    /**
     * Periodically checks the finished assignments and
     * possibly delivers the solutions not yet carried out by the students.
     */
    void assignmentExpired();

}
