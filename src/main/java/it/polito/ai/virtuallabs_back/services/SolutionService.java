package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface SolutionService {

    /**
     * @param id
     * @return
     */
    Optional<SolutionDTO> getSolution(Long id);

    /**
     * @param id
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<SolutionDTO> getSolutionsByAssignment(Long id);

    /**
     * @param courseName
     * @return
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<SolutionDTO> getSolutionsByStudent(String courseName);

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
    SolutionDTO reviewSolution(SolutionDTO solutionDTO);

    /**
     * @param solutionDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    SolutionDTO setActive(SolutionDTO solutionDTO);

    /**
     * @param solutionDTO
     * @param vote
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    SolutionDTO setVote(SolutionDTO solutionDTO, String vote);
}
