package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    StudentService studentService;

    @GetMapping("/{studentSerial}")
    public StudentDTO getStudent(@PathVariable String studentSerial) {
        if (!studentService.getStudent(studentSerial).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, studentSerial);
        return ModelHelper.enrich(studentService.getStudent(studentSerial).get());
    }

    @GetMapping({"", "/"})
    public List<StudentDTO> getAllStudents() {
        return studentService.getAllStudents()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/enrolled")
    public List<StudentDTO> getEnrolledStudents(@PathVariable String courseName) {
        return studentService.getEnrolledStudents(courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/solutions/{solutionId}")
    public StudentDTO getSolutionStudent(@PathVariable Long solutionId) {
        return ModelHelper.enrich(studentService.getSolutionStudent(solutionId));
    }


    @GetMapping("/{courseName}/available")
    public List<StudentDTO> getAvailableStudents(@PathVariable String courseName) {
        return studentService.getAvailableStudents(courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/engaged")
    public List<StudentDTO> getEngagedStudents(@PathVariable String courseName) {
        return studentService.getEngagedStudents(courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{teamId}/members")
    public List<StudentDTO> getTeamStudents(@PathVariable Long teamId) {
        return studentService.getTeamStudents(teamId)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{vmId}/owners")
    public List<StudentDTO> getVmOwners(@PathVariable Long vmId) {
        return studentService.getVmOwners(vmId)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping("/{courseName}/enroll")
    public StudentDTO addStudentToCourse(@PathVariable String courseName, @RequestBody Map<String, String> map) {
        if (!map.containsKey("serial") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must contains only serial");
        return ModelHelper.enrich(studentService.addStudentToCourse(map.get("serial"), courseName));
    }

    @PostMapping("/{courseName}/enrollAll")
    public List<StudentDTO> enrollAll(@PathVariable String courseName, @RequestBody Map<String, Object> map) {
        if (!map.containsKey("serials") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return studentService.enrollAll((List<String>) map.get("serials"), courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping("/{courseName}/enrollCsv")
    public List<StudentDTO> enrollCsv(@PathVariable String courseName, @RequestParam("file") MultipartFile file) {
        if (!file.getContentType().equals("text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            return studentService.enrollCsv(reader, courseName)
                    .stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/uploadImage")
    public StudentDTO uploadImage(@RequestParam(value = "imageFile") MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        return studentService.uploadImage(file.getBytes());
    }

    @DeleteMapping("/{courseName}/deleteStudent/{studentSerial}")
    @ResponseStatus(code = HttpStatus.OK, reason = "Student deleted")
    public void deleteStudentFromCourse(@PathVariable String courseName, @PathVariable String studentSerial) {
        studentService.deleteStudentFromCourse(studentSerial, courseName);
    }
}
