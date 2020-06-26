package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
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
        if (!courseService.addCourse(courseDTO))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A course with name " + courseDTO.getName() + " already exists");

        return ModelHelper.enrich(courseDTO);
    }

    @PostMapping("/{name}/enrollOne")
    @ResponseStatus(code = HttpStatus.OK, reason = "Student enrolled")
    public void enrollStudent(@PathVariable String name, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");

        if (!courseService.addStudentToCourse(map.get("id"), name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course is not enabled");
    }

    @PostMapping("/{name}/assignTeacher")
    @ResponseStatus(code = HttpStatus.OK, reason = "Teacher assigned")
    public void assignTeacher(@PathVariable String name, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");

        if (!courseService.addTeacherToCourse(map.get("id"), name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, map.get("id"));
    }

    @PostMapping("/{name}/enrollAll")
    public List<Boolean> enrollAll(@PathVariable String name, @RequestBody Map<String, Object> map) {
        if (!map.containsKey("ids") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return courseService.enrollAll((List<String>) map.get("ids"), name);
    }

    @PostMapping("/{name}/enrollAllCsv")
    public List<Boolean> enrollAllCsv(@PathVariable String name, @RequestParam("file") MultipartFile file) {
        if (!file.getContentType().equals("text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            return courseService.enrollCsv(reader, name);
        } catch (IOException ioe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/modify")
    @ResponseStatus(code = HttpStatus.OK, reason = "Course correctly modified")
    public void modifyCourse(@Valid @RequestBody CourseDTO courseDTO) {
        if (!courseService.modifyCourse(courseDTO))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    /*@PutMapping("/{name}/enable")
    public void enableCourse(@PathVariable String name) {
        courseService.enableCourse(name);
    }

    @PutMapping("/{name}/disable")
    public void disableCourse(@PathVariable String name) {
        courseService.disableCourse(name);
    }*/

    @DeleteMapping("/{name}/delete")
    public void deleteCourse(@PathVariable String name) {
        courseService.deleteCourse(name);
    }

    @DeleteMapping("/{name}/deleteStudent")
    @ResponseStatus(code = HttpStatus.OK, reason = "Student deleted")
    public void deleteStudentFromCourse(@PathVariable String name, @RequestBody Map<String, String> map) {
        if (!map.containsKey("id") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only id");

        if (!courseService.deleteStudentFromCourse(map.get("id"), name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, map.get("id"));
    }

}
