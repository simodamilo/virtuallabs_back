package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @GetMapping("/students")
    public List<TeamDTO> getStudentTeams() {
        return teamService.getStudentTeams()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/courses/{courseName}")
    public List<TeamDTO> getCourseTeams(@PathVariable String courseName) {
        return teamService.getCourseTeams(courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping("/{courseName}")
    public TeamDTO addTeam(@PathVariable String courseName, @RequestBody Map<String, Object> map) {
        if (!map.containsKey("name") || !map.containsKey("ids") || map.keySet().size() != 2)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return teamService.proposeTeam(courseName, map.get("name").toString(), (List<String>) map.get("ids"));
    }

}
