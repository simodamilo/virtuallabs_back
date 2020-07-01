package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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


    @PutMapping("/uploadImage")
    public TeacherDTO uploadImage(@RequestParam(value = "imageFile") MultipartFile file) throws IOException {
        if (!file.getContentType().split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        return teacherService.uploadImage(file.getBytes());
    }

}
