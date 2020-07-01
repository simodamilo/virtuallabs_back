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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/teachers")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @GetMapping("/{teacherSerial}")
    public TeacherDTO getTeacher(@PathVariable String teacherSerial) {
        if (!teacherService.getTeacher(teacherSerial).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, teacherSerial);
        return ModelHelper.enrich(teacherService.getTeacher(teacherSerial).get());
    }

    @GetMapping({"", "/"})
    public List<TeacherDTO> getAllTeachers() {
        return teacherService.getAllTeachers().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    @PostMapping("/{courseName}/assign")
    public TeacherDTO addTeacherToCourse(@PathVariable String courseName, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");
        return ModelHelper.enrich(teacherService.addTeacherToCourse(map.get("id"), courseName));
    }

    @PutMapping("/uploadImage")
    public TeacherDTO uploadImage(@RequestParam(value = "imageFile") MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        return teacherService.uploadImage(file.getBytes());
    }
}
