package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamTokenDTO;
import it.polito.ai.virtuallabs_back.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @GetMapping("/{courseName}/student")
    public TeamDTO getStudentTeamByCourse(@PathVariable String courseName) {
        if (!teamService.getStudentTeamByCourse(courseName).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No team available");
        return ModelHelper.enrich(teamService.getStudentTeamByCourse(courseName).get());
    }

    @GetMapping("/students/active")
    public List<TeamDTO> getStudentTeams() {
        return teamService.getStudentTeams()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/students/{courseName}/pending")
    public List<TeamDTO> getStudentPendingTeams(@PathVariable String courseName) {
        return teamService.getStudentPendingTeams(courseName)
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
    public TeamDTO proposeTeam(@PathVariable String courseName, @RequestBody Map<String, Object> map) {
        if (!map.containsKey("name"))
            System.out.println("Method called: " + map);
        if (!map.containsKey("name") || !map.containsKey("serials") || !map.containsKey("timeout") || map.keySet().size() != 3)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return teamService.proposeTeam(courseName, map.get("name").toString(), Integer.parseInt(map.get("timeout").toString()), (List<String>) map.get("serials"));
    }

    @PutMapping({"", "/"})
    public TeamDTO setTeamParams(@Valid @RequestBody TeamDTO teamDTO) {
        return teamService.setTeamParams(teamDTO);
    }

    @PutMapping("/accept")
    public TeamDTO acceptTeam(@Valid @RequestBody TeamTokenDTO teamTokenDTO) {
        return teamService.acceptTeam(teamTokenDTO);
    }

    @PutMapping("/reject")
    @ResponseStatus(code = HttpStatus.OK, reason = "Teams rejected")
    public void rejectTeam(@Valid @RequestBody TeamTokenDTO teamTokenDTO) {
        teamService.rejectTeam(teamTokenDTO);
    }

}
