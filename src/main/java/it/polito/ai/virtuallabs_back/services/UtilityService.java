package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import it.polito.ai.virtuallabs_back.entities.*;

public interface UtilityService {

    /**
     * Used to get an assignment.
     *
     * @param assignmentId of the desired assignment.
     * @return the searched assignment.
     */
    Assignment getAssignment(Long assignmentId);

    /**
     * Used to get a course.
     *
     * @param courseName of the desired course.
     * @return the searched course.
     */
    Course getCourse(String courseName);

    /**
     * Used to get a modelVm.
     *
     * @param modelVmId of the desired modelVm.
     * @return the searched modelVm.
     */
    ModelVM getModelVm(Long modelVmId);

    /**
     * Used to get a solution.
     *
     * @param solutionId of the desired solution.
     * @return the searched solution.
     */
    Solution getSolution(Long solutionId);

    /**
     * Used to get the authenticated student.
     *
     * @return the authenticated student.
     */
    Student getStudent();

    /**
     * Used to get the authenticated teacher.
     *
     * @return the authenticated teacher.
     */
    Teacher getTeacher();

    /**
     * Used to get a team.
     *
     * @param teamId of the desired team.
     * @return the searched team.
     */
    Team getTeam(Long teamId);

    /**
     * Used to get a vm.
     *
     * @param vmId of the desired vm.
     * @return the searched vm.
     */
    VM getVm(Long vmId);

    /**
     * Used to check if a teacher is the owner of the course
     *
     * @param courseName of the course that must be checked.
     */
    void courseOwnerValid(String courseName);

    /**
     * Used to check if the constraint of a team are respected.
     *
     * @param vmDTO  the vm that is added/modified.
     * @param teamId in which the vm is added/modified.
     */
    void constraintsCheck(VMDTO vmDTO, Long teamId);

    /**
     * Used to check if the authenticated user is a teacher.
     *
     * @return true if the authenticated user is a teacher.
     */
    boolean isTeacher();
}
