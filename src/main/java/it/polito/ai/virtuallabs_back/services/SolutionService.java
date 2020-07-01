package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface SolutionService {

    /**
     * @param solutionId
     * @return
     */
    Optional<SolutionDTO> getSolution(Long solutionId);

    /**
     * @param assignmentId
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<SolutionDTO> getAssignmentSolutions(Long assignmentId);

    /**
     * @param courseName
     * @return
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<SolutionDTO> getStudentSolutions(String courseName);

    /**
     * @param solutionDTO
     * @return
     */
    @PreAuthorize("hasRole('STUDENT')")
    SolutionDTO addSolution(SolutionDTO solutionDTO, Long assignmentId);

    /**
     * @param solutionDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    SolutionDTO addSolutionReview(SolutionDTO solutionDTO);

    /**
     * @param solutionDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    SolutionDTO setModifiable(SolutionDTO solutionDTO);

    /**
     * @param solutionDTO
     * @param grade
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    SolutionDTO setGrade(SolutionDTO solutionDTO, String grade);
}
