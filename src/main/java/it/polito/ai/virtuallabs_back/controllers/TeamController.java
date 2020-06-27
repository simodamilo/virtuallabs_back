package it.polito.ai.virtuallabs_back.controllers;


import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
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

    @GetMapping("/{teamId}/members")
    public List<StudentDTO> getTeamMembers(@PathVariable Long teamId) {
        return teamService.getMembers(teamId)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping("/{name}/proposeTeam")
    public TeamDTO addTeam(@PathVariable String name, @RequestBody Map<String, Object> map) {
        if (!map.containsKey("name") || !map.containsKey("ids") || map.keySet().size() != 2)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return teamService.proposeTeam(name, map.get("name").toString(), (List<String>) map.get("ids"));
    }

}
