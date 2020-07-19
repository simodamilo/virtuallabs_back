package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    /**
     * Used to get an assignment by the Id.
     *
     * @param assignmentId of the desired assignment.
     * @return empty optional if the assignment misses.
     */
    Optional<AssignmentDTO> getAssignment(Long assignmentId);

    /**
     * Used to get the list of assignments of a course.
     *
     * @param courseName in which assignments are searched.
     * @return list of found assignments.
     */
    List<AssignmentDTO> getCourseAssignments(String courseName);

    /**
     * Used by the teacher to add an assignment to the course and
     * generate a empty solution for each student of the course.
     *
     * @param assignmentDTO which needs to be added.
     * @param courseName    in which assignments are searched.
     * @return the added assignment.
     */
    @PreAuthorize("hasRole('TEACHER')")
    AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, String courseName);

    /**
     * Used by the teacher add the content to an assignment.
     *
     * @param assignmentId of the assignment modified.
     * @param file         content of the assignment.
     * @return the modified assignment.
     */
    @PreAuthorize("hasRole('TEACHER')")
    AssignmentDTO addContent(Long assignmentId, MultipartFile file);

    /**
     * Periodically checks the finished assignments and
     * possibly delivers the solutions not yet carried out by the students.
     */
    void assignmentExpired();

}
