package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    CourseService courseService;

    @GetMapping("/{courseName}")
    public CourseDTO getCourse(@PathVariable String courseName) {
        if (!courseService.getCourse(courseName).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, courseName);
        return ModelHelper.enrich(courseService.getCourse(courseName).get());
    }

    @GetMapping({"", "/"})
    public List<CourseDTO> getAllCourses() {
        return courseService.getAllCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/teachers")
    public List<CourseDTO> getTeacherCourses() {
        return courseService.getTeacherCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/students")
    public List<CourseDTO> getStudentCourses() {
        return courseService.getStudentCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping({"", "/"})
    public CourseDTO addCourse(@Valid @RequestBody CourseDTO courseDTO) {
        return ModelHelper.enrich(courseService.addCourse(courseDTO));
    }

    @PutMapping({"", "/"})
    public CourseDTO modifyCourse(@Valid @RequestBody CourseDTO courseDTO) {
        return ModelHelper.enrich(courseService.modifyCourse(courseDTO));
    }

    @DeleteMapping("/{courseName}")
    @ResponseStatus(code = HttpStatus.OK, reason = "Course correctly deleted")
    public void deleteCourse(@PathVariable String courseName) {
        courseService.deleteCourse(courseName);
    }
}
