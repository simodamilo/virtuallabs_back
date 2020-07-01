package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface TeamService {

    /**
     * @return
     */
    List<TeamDTO> getStudentTeams();

    /**
     * @return
     */
    List<TeamDTO> getStudentPendingTeams();

    /**
     * With the getTeamsForCourse method all teams of the specific course are returned.
     *
     * @param courseName it is the course for which the user want the list of teams.
     * @return list of all teams inside the course.
     */
    List<TeamDTO> getCourseTeams(String courseName);

    /**
     * @param courseName
     * @param name
     * @param memberIds
     * @return
     */
    @PreAuthorize("hasRole('STUDENT')")
    TeamDTO proposeTeam(String courseName, String name, List<String> memberIds);

    /**
     * @param teamId
     * @return
     */
    TeamDTO acceptTeam(Long teamId);

    /**
     * @param teamId
     */
    void rejectTeam(Long teamId);

    /**
     * @param teamDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeamDTO setTeamParams(TeamDTO teamDTO);

    /**
     *
     */
    void clearToken();
}
