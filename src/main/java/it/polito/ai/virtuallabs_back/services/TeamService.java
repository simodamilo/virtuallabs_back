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
     * @param teamDTO
     * @return
     */
    TeamDTO acceptTeam(TeamDTO teamDTO);

    /**
     * @param teamDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeamDTO setTeamParams(TeamDTO teamDTO);

    /**
     * @param teamId
     */
    @PreAuthorize("hasRole('STUDENT')")
    void enableTeam(Long teamId);


    void evictTeam(Long teamId);

    /*void clearToken();*/

}
