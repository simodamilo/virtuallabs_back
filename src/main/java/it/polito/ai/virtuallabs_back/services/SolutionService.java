package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface SolutionService {

    /**
     * Used to get a solution by the Id.
     *
     * @param solutionId of the desired solution.
     * @return empty optional if the solution misses.
     */
    Optional<SolutionDTO> getSolution(Long solutionId);

    /**
     * Used to get the list of solution of an assignment.
     *
     * @param assignmentId of the assignment in which solutions  are searched.
     * @return list of found solution.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<SolutionDTO> getAssignmentSolutions(Long assignmentId);

    /**
     * Used to get the list of solution for a specific course.
     *
     * @param courseName in which solutions are searched.
     * @return list of found solution.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<SolutionDTO> getCourseSolutions(String courseName);

    /**
     * Used to get the list of solution of an student for a specific course.
     *
     * @param courseName in which solutions are searched.
     * @return list of found solution.
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<SolutionDTO> getStudentSolutions(String courseName);

    /**
     * Used by the student to add a solution to the assignment.
     *
     * @param solutionDTO  which needs to be added.
     * @param assignmentId to which the solution refers.
     * @return the added solution.
     */
    @PreAuthorize("hasRole('STUDENT')")
    SolutionDTO addSolution(SolutionDTO solutionDTO, Long assignmentId);

    /**
     * Used by the teacher to add the review of a solution.
     *
     * @param solutionDTO that contains the review of the teacher.
     * @return the solution if the review is correctly added.
     */
    @PreAuthorize("hasRole('TEACHER')")
    SolutionDTO addSolutionReview(SolutionDTO solutionDTO);

    /**
     * Used by the teacher to enable/disable the possibilities
     * to modify a solution.
     *
     * @param solutionDTO which is modified.
     * @return the modified solution if works properly.
     */
    @PreAuthorize("hasRole('TEACHER')")
    SolutionDTO setModifiable(SolutionDTO solutionDTO);

    /**
     * Used by the teacher to add the grade.
     *
     * @param solutionDTO which is evaluated.
     * @param grade       of the solution.
     * @return the modified solution if works properly.
     */
    @PreAuthorize("hasRole('TEACHER')")
    SolutionDTO setGrade(SolutionDTO solutionDTO, String grade);

}
