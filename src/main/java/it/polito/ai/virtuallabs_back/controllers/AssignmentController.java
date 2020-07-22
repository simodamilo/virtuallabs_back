package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs_back.services.AssignmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/API/assignments")
public class AssignmentController {

    @Autowired
    AssignmentService assignmentService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/{assignmentId}")
    public byte[] getAssignmentContent(@PathVariable Long assignmentId) {
        return assignmentService.getAssignmentContent(assignmentId);
    }

    @GetMapping("/courses/{courseName}")
    public List<AssignmentDTO> getCourseAssignments(@PathVariable String courseName) {
        return assignmentService.getCourseAssignments(courseName);
    }


    @PostMapping("/{courseName}")
    public AssignmentDTO addAssignment(@Valid @RequestBody AssignmentDTO assignmentDTO,
                                       @PathVariable String courseName) {
        return assignmentService.addAssignment(assignmentDTO, courseName);
    }

    @PutMapping("/{assignmentId}")
    public AssignmentDTO addContent(@RequestParam(value = "imageFile") MultipartFile file,
                                    @PathVariable Long assignmentId) {
        if (!Objects.requireNonNull(file.getContentType()).split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        return assignmentService.addContent(assignmentId, file);
    }
}
