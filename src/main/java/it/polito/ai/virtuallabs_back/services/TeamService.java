package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamTokenDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    /**
     * Used to get the team of the authenticated student.
     *
     * @param courseName in which the team is searched.
     * @return empty optional if the team does not exists.
     */
    @PreAuthorize("hasRole('STUDENT')")
    Optional<TeamDTO> getStudentTeamByCourse(String courseName);

    /**
     * Used by the teacher to get the team of a student for a specific course.
     *
     * @param courseName    in which the team is searched.
     * @param studentSerial of the desired student.
     * @return empty optional if the team does not exists.
     */
    @PreAuthorize("hasRole('TEACHER')")
    Optional<TeamDTO> getStudentTeamByCourseAndSerial(String courseName, String studentSerial);

    /**
     * Used to get the list of pending teams request of a student.
     *
     * @param courseName in which teams are searched.
     * @return list of found teams.
     */
    List<TeamDTO> getStudentPendingTeams(String courseName);

    /**
     * Used to get the list of active teams for a specific course.
     *
     * @param courseName in which teams are searched.
     * @return list of all teams inside the course.
     */
    List<TeamDTO> getCourseTeams(String courseName);

    /**
     * Used to add a new team, checks the constraints, generates the team
     * and a token for each student invited.
     *
     * @param courseName     in which team is inserted.
     * @param teamName       decided by the student.
     * @param timeout        for token requests.
     * @param studentSerials list of the members of the team.
     * @return the team to confirm the creation.
     */
    @PreAuthorize("hasRole('STUDENT')")
    TeamDTO proposeTeam(String courseName, String teamName, int timeout, List<String> studentSerials);

    /**
     * Used to set or update the parameters of a team.
     *
     * @param teamDTO contains the new values of the parameters.
     * @return the updated team.
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeamDTO setTeamParams(TeamDTO teamDTO);

    /**
     * Used to accept an invitation to a group.
     *
     * @param teamTokenDTO of the student inside the team.
     * @return the team, if all students accepted status is 1.
     */
    @PreAuthorize("hasRole('STUDENT')")
    TeamDTO acceptTeam(TeamTokenDTO teamTokenDTO);

    /**
     * Used to reject an invitation to a group.
     *
     * @param teamTokenDTO of the student inside the team.
     */
    @PreAuthorize("hasRole('STUDENT')")
    void rejectTeam(TeamTokenDTO teamTokenDTO);

    /**
     * Used by the teacher to delete a team.
     *
     * @param teamId of the team that is deleted.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteTeam(Long teamId);

    /**
     * Periodically checks the expired teamToken and
     * delete the relative team when needed.
     */
    void clearTeamToken();
}
