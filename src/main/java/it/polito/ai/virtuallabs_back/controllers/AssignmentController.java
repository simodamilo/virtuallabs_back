package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs_back.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/API/assignments")
public class AssignmentController {

    @Autowired
    AssignmentService assignmentService;

    @GetMapping("/courses/{name}")
    public List<AssignmentDTO> allByCourse(@PathVariable String name) {
        return assignmentService.getAssignmentsByCourse(name);
    }

    @GetMapping("/{id}")
    public AssignmentDTO getOne(@PathVariable Long id) {
        if (!assignmentService.getAssignment(id).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, id.toString());
        return assignmentService.getAssignment(id).get();
    }

    @PostMapping("/{courseName}")
    public AssignmentDTO addAssignment(@Valid @RequestBody AssignmentDTO dto, @PathVariable String courseName) {
        return assignmentService.addAssignment(dto, courseName);
    }

    @PutMapping({"", "/"})
    public AssignmentDTO modifyAssignment(@Valid @RequestBody AssignmentDTO dto) {
        return assignmentService.modifyAssignment(dto);
    }
}
