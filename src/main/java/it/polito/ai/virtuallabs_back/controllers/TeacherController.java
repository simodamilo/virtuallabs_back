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

    @GetMapping("/{teacherSerial}/getOne")
    public TeacherDTO getTeacher(@PathVariable String teacherSerial) {
        if (!teacherService.getTeacher(teacherSerial).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The teacher you are looking for does not exist");
        return ModelHelper.enrich(teacherService.getTeacher(teacherSerial).get());
    }

    @GetMapping("/{teacherSerial}/image")
    public byte[] getTeacherImage(@PathVariable String teacherSerial) {
        return teacherService.getTeacherImage(teacherSerial);
    }

    @GetMapping("/{courseName}/getAll")
    public List<TeacherDTO> getAllTeachers(@PathVariable String courseName) {
        return teacherService.getAllTeachers(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/getOwners")
    public List<TeacherDTO> getCourseOwners(@PathVariable String courseName) {
        return teacherService.getCourseOwners(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    @PostMapping("/{courseName}/assign")
    public TeacherDTO addTeacherToCourse(@PathVariable String courseName, @RequestBody Map<String, String> map) {
        if (!map.containsKey("serial") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request is not correct");
        return ModelHelper.enrich(teacherService.addTeacherToCourse(map.get("serial"), courseName));
    }

    @PutMapping("/uploadImage")
    public byte[] uploadImage(@RequestParam(value = "imageFile") MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File type not supported");
        return teacherService.uploadImage(file.getBytes());
    }

    @DeleteMapping("/{courseName}/deleteTeacher/{teacherSerial}")
    @ResponseStatus(code = HttpStatus.OK, reason = "Teacher deleted")
    public void deleteTeacherFromCourse(@PathVariable String courseName, @PathVariable String teacherSerial) {
        teacherService.deleteTeacherFromCourse(teacherSerial, courseName);
    }
}
