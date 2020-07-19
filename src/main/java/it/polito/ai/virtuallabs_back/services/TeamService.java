package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    /**
     * Used to get the team of the authenticated student.
     *
     * @param courseName of the student team.
     * @return the searched team.
     */
    @PreAuthorize("hasRole('STUDENT')")
    Optional<TeamDTO> getStudentTeamByCourse(String courseName);

    /**
     * Used to get the list of active teams of a student.
     *
     * @return list of teams found.
     */
    List<TeamDTO> getStudentTeams();

    /**
     * Used to get the list of pending teams request of a student.
     *
     * @return list of teams found.
     */
    List<TeamDTO> getStudentPendingTeams();

    /**
     * Used to get the list of active teams of the specific course.
     *
     * @param courseName in which teams are searched.
     * @return list of all teams inside the course.
     */
    List<TeamDTO> getCourseTeams(String courseName);

    /**
     * Used to add a new team, checks the constraints generates the team
     * and a token for each student invited.
     *
     * @param courseName     in which teams are searched.
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
     * @param teamDTO contains the new vale for the parameters.
     * @return the updated team.
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeamDTO setTeamParams(TeamDTO teamDTO);

    /**
     * Used to accept an invitation to a group.
     *
     * @param teamId of the proposed team.
     * @return the team, if all students accepted status = 1.
     */
    @PreAuthorize("hasRole('STUDENT')")
    TeamDTO acceptTeam(Long teamId);

    /**
     * Used to reject an invitation to a group
     *
     * @param teamId of the proposed team.
     */
    @PreAuthorize("hasRole('STUDENT')")
    void rejectTeam(Long teamId);

    /**
     * Periodically checks the expired teamToken and
     * delete the relative team when needed.
     */
    void clearTeamToken();
}
