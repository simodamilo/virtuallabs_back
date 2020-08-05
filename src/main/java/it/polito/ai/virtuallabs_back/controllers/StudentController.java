package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamTokenDTO;
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

    @GetMapping("/{studentSerial}/getOne")
    public StudentDTO getStudent(@PathVariable String studentSerial) {
        if (!studentService.getStudent(studentSerial).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The student you are looking for does not exist");
        return ModelHelper.enrich(studentService.getStudent(studentSerial).get());
    }

    @GetMapping("/{studentSerial}/image")
    public byte[] getStudentImage(@PathVariable String studentSerial) {
        return studentService.getStudentImage(studentSerial);
    }

    @GetMapping("/{courseName}/getAll")
    public List<StudentDTO> getAllStudents(@PathVariable String courseName) {
        return studentService.getAllStudents(courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/getEnrolled")
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

    @GetMapping("{teamId}/{studentSerial}/token")
    public TeamTokenDTO getStudentTeamStatus(@PathVariable Long teamId, @PathVariable String studentSerial) {
        return studentService.getStudentTeamStatus(teamId, studentSerial);
    }

    @PostMapping("/{courseName}/enroll")
    public StudentDTO addStudentToCourse(@PathVariable String courseName, @RequestBody Map<String, String> map) {
        if (!map.containsKey("serial") || map.keySet().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request is not correct");
        return ModelHelper.enrich(studentService.addStudentToCourse(map.get("serial"), courseName));
    }

    @PostMapping("/{courseName}/enrollCsv")
    public List<StudentDTO> enrollCsv(@PathVariable String courseName, @RequestParam("file") MultipartFile file) {
        if (!Objects.equals(file.getContentType(), "text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File type not supported");
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
    public byte[] uploadImage(@RequestParam(value = "imageFile") MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File type noy supported");
        return studentService.uploadImage(file.getBytes());
    }

    @DeleteMapping("/{courseName}/deleteStudent/{studentSerial}")
    @ResponseStatus(code = HttpStatus.OK, reason = "Student deleted")
    public void deleteStudentFromCourse(@PathVariable String courseName, @PathVariable String studentSerial) {
        studentService.deleteStudentFromCourse(studentSerial, courseName);
    }
}
