package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/teachers")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @GetMapping({"", "/"})
    public List<TeacherDTO> getAll() {
        return teacherService.getAllTeachers().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TeacherDTO getTeacher(@PathVariable String id) {
        if (!teacherService.getTeacher(id).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, id);

        return ModelHelper.enrich(teacherService.getTeacher(id).get());
    }

}
