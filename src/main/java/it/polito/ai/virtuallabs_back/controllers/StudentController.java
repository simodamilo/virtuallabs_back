package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    public List<StudentDTO> all() {
        return teamService.getAllStudents().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public StudentDTO getOne(@PathVariable String id) {
        if (!teamService.getStudent(id).isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, id);
        return ModelHelper.enrich(teamService.getStudent(id).get());
    }

    @GetMapping("/courses")
    public List<CourseDTO> getCourses() {
        return teamService.getCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/teams")
    public List<TeamDTO> getStudentTeams() {
        return teamService.getTeamsForStudent()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{teamId}/members")
    public List<StudentDTO> getTeamMembers(@PathVariable Long teamId) {
        return teamService.getMembers(teamId)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping({"", "/"})
    public StudentDTO addStudent(@Valid @RequestBody StudentDTO dto) {
        if (!teamService.addStudent(dto)) throw new ResponseStatusException(HttpStatus.CONFLICT, dto.getSerial());
        return ModelHelper.enrich(dto);
    }

    @PostMapping({"/addAll"})
    public List<Boolean> addAll(@RequestBody Map<String, @Valid StudentDTO> map) {
        List<StudentDTO> list = new ArrayList<>();
        map.forEach((s, studentDTO) -> list.add(studentDTO));
        return teamService.addAll(list);
    }
}
