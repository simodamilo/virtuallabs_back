package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface TeamService {

    List<StudentDTO> getMembers(Long teamId);

    @PreAuthorize("hasRole('STUDENT')")
    TeamDTO proposeTeam(String courseName, String name, List<String> memberIds);

    void enableTeam(Long teamId);

    void evictTeam(Long teamId);

    void clearToken();

}
