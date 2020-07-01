package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.services.StudentService;
import it.polito.ai.virtuallabs_back.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    TeamService teamService;

    @Autowired
    StudentService studentService;

    @GetMapping({"", "/"})
    public List<StudentDTO> all() {
        return studentService.getAllStudents().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public StudentDTO getOne(@PathVariable String id) {
        if (!studentService.getStudent(id).isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, id);
        return ModelHelper.enrich(studentService.getStudent(id).get());
    }

    @GetMapping("/courses")
    public List<CourseDTO> getCourses() {
        return studentService.getCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/teams")
    public List<TeamDTO> getStudentTeams() {
        return studentService.getTeamsForStudent()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}/available")
    public List<StudentDTO> getAvailableStudents(@PathVariable String name) {
        return studentService.getAvailableStudents(name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}/engaged")
    public List<StudentDTO> getEngagedStudents(@PathVariable String name) {
        return studentService.getStudentsInTeams(name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PutMapping("/uploadImage")
    public StudentDTO uploadImage(@RequestParam(value = "imageFile") MultipartFile file) throws IOException {
        if (!file.getContentType().split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        return studentService.uploadImage(file.getBytes());
    }
}
