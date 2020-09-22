package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SolutionService {

    /**
     * Used to get the content of a solution by the Id.
     *
     * @param solutionId of the desired solution.
     * @return the content of the solution.
     */
    byte[] getSolutionContent(Long solutionId);

    /**
     * Used to get the list of solution of an assignment.
     *
     * @param assignmentId of the assignment in which solutions are searched.
     * @return list of found solutions.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<SolutionDTO> getAssignmentSolutions(Long assignmentId);

    /**
     * Used by the teacher or student to get the list of solution of an student for a specific assignment.
     *
     * @param assignmentId of the solutions searched.
     * @param studentId    of the solutions searched.
     * @return list of found solutions.
     */
    List<SolutionDTO> getStudentSolutions(Long assignmentId, String studentId);


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
    SolutionDTO addSolutionReview(SolutionDTO solutionDTO, Long assignmentId, String studentSerial);

    /**
     * Used to add the content of a solution.
     *
     * @param solutionId which is modified.
     * @param file       content of the solution.
     * @return the modified solution if works properly.
     */
    SolutionDTO addContent(Long solutionId, MultipartFile file);

    /**
     * Used to remove the solution if there is an error with the content
     * to prevent inconsistency in the database.
     *
     * @param solutionId of the deleted solution.
     */
    void deleteSolution(Long solutionId);

}
