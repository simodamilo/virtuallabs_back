package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    public List<CourseDTO> all() {
        return teamService.getAllCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public CourseDTO getOne(@PathVariable String name) {
        if (!teamService.getCourse(name).isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, name);
        return ModelHelper.enrich(teamService.getCourse(name).get());
    }

    @GetMapping("/{name}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable String name) {
        return teamService.getEnrolledStudents(name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());

    }

    @GetMapping("/{name}/teams")
    public List<TeamDTO> getCourseTeams(@PathVariable String name) {
        return teamService.getTeamForCourse(name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}/available")
    public List<StudentDTO> getAvailableStudents(@PathVariable String name) {
        return teamService.getAvailableStudents(name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}/engaged")
    public List<StudentDTO> getEngagedStudents(@PathVariable String name) {
        return teamService.getStudentsInTeams(name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping({"", "/"})
    public CourseDTO addCourse(@Valid @RequestBody CourseDTO courseDTO) {
        if (!teamService.addCourse(courseDTO))
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseDTO.getName());
        return ModelHelper.enrich(courseDTO);
    }

    @PostMapping("/{name}/enrollOne")
    @ResponseStatus(code = HttpStatus.OK, reason = "Student enrolled")
    public void enrollStudent(@PathVariable String name, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");
        if (!teamService.addStudentToCourse(map.get("id"), name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, map.get("id"));
    }

    @PostMapping("/{name}/assignTeacher")
    @ResponseStatus(code = HttpStatus.OK, reason = "Teacher assigned")
    public void assignTeacher(@PathVariable String name, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");
        if (!teamService.addTeacherToCourse(map.get("id"), name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, map.get("id"));
    }

    @PostMapping("/{name}/enrollAll")
    public List<Boolean> enrollAll(@PathVariable String name, @RequestBody Map<String, Object> map) {
        if (!map.containsKey("ids") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return teamService.enrollAll((List<String>) map.get("ids"), name);
    }

    @PostMapping("/{name}/enrollMany")
    public List<Boolean> addAndEnrollStudents(@PathVariable String name, @RequestParam("file") MultipartFile file) {
        if (!file.getContentType().equals("text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return teamService.addAndEnroll(reader, name);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/{name}/proposeTeam")
    public TeamDTO addTeam(@PathVariable String name, @RequestBody Map<String, Object> map) {
        if (!map.containsKey("name") || !map.containsKey("ids") || map.keySet().size() != 2)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return teamService.proposeTeam(name, map.get("name").toString(), (List<String>) map.get("ids"));
    }

    @PutMapping("/{name}/enable")
    public void enableCourse(@PathVariable String name) {
        teamService.enableCourse(name);
    }

    @PutMapping("/{name}/disable")
    public void disableCourse(@PathVariable String name) {
        teamService.disableCourse(name);
    }

}
