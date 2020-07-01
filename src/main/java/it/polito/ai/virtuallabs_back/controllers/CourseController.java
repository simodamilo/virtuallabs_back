package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
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
    CourseService courseService;


    @GetMapping("/{name}")
    public CourseDTO getOne(@PathVariable String name) {
        if (!courseService.getCourse(name).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, name);

        return ModelHelper.enrich(courseService.getCourse(name).get());
    }

    @GetMapping({"", "/"})
    public List<CourseDTO> getAll() {
        return courseService.getAllCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}/enrolled")
    public List<StudentDTO> getEnrolledStudents(@PathVariable String name) {
        return courseService.getEnrolledStudents(name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}/teams")
    public List<TeamDTO> getCourseTeams(@PathVariable String name) {
        return courseService.getTeamsForCourse(name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/teacherCourses")
    public List<CourseDTO> getTeacherCourses() {
        return courseService.getTeacherCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }


    @PostMapping({"", "/"})
    public CourseDTO addCourse(@Valid @RequestBody CourseDTO courseDTO) {
        return ModelHelper.enrich(courseService.addCourse(courseDTO));
    }

    @PostMapping("/{name}/enrollOne")
    @ResponseStatus(code = HttpStatus.OK, reason = "Student enrolled")
    public StudentDTO enrollStudent(@PathVariable String name, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");

        return ModelHelper.enrich(courseService.addStudentToCourse(map.get("id"), name));
    }

    @PostMapping("/{name}/assignTeacher")
    @ResponseStatus(code = HttpStatus.OK, reason = "Teacher assigned")
    public TeacherDTO assignTeacher(@PathVariable String name, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");

        return ModelHelper.enrich(courseService.addTeacherToCourse(map.get("id"), name));
    }

    @PostMapping("/{name}/enrollAll")
    public List<StudentDTO> enrollAll(@PathVariable String name, @RequestBody Map<String, Object> map) {
        if (!map.containsKey("ids") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return courseService.enrollAll((List<String>) map.get("ids"), name)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping("/{name}/enrollAllCsv")
    public List<StudentDTO> enrollAllCsv(@PathVariable String name, @RequestParam("file") MultipartFile file) {
        if (!file.getContentType().equals("text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            return courseService.enrollCsv(reader, name)
                    .stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }


    @PutMapping("/modify")
    @ResponseStatus(code = HttpStatus.OK, reason = "Course correctly modified")
    public CourseDTO modifyCourse(@Valid @RequestBody CourseDTO courseDTO) {
        return ModelHelper.enrich(courseService.modifyCourse(courseDTO));
    }


    @DeleteMapping("/{name}/delete")
    @ResponseStatus(code = HttpStatus.OK, reason = "Course correctly deleted")
    public void deleteCourse(@PathVariable String name) {
        courseService.deleteCourse(name);
    }

    @DeleteMapping("/{name}/deleteStudent")
    @ResponseStatus(code = HttpStatus.OK, reason = "Student deleted")
    public void deleteStudentFromCourse(@PathVariable String name, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");

        courseService.deleteStudentFromCourse(map.get("id"), name);
    }

}
