package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import it.polito.ai.virtuallabs_back.services.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/API/solutions")
public class SolutionController {

    @Autowired
    SolutionService solutionService;

    @GetMapping("/{solutionId}")
    public SolutionDTO getSolution(@PathVariable Long solutionId) {
        if (!solutionService.getSolution(solutionId).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, solutionId.toString());
        return solutionService.getSolution(solutionId).get();
    }

    @GetMapping("/assignments/{assignmentId}")
    public List<SolutionDTO> getAssignmentSolutions(@PathVariable Long assignmentId) {
        return solutionService.getAssignmentSolutions(assignmentId);
    }

    @GetMapping("/courses/{courseName}")
    public List<SolutionDTO> getStudentSolutions(@PathVariable String courseName) {
        return solutionService.getStudentSolutions(courseName);
    }

    @PostMapping("/{assignmentId}")
    public SolutionDTO addSolution(@RequestBody SolutionDTO solutionDTO, @PathVariable Long assignmentId) {
        return solutionService.addSolution(solutionDTO, assignmentId);
    }

    @PostMapping("/review")
    public SolutionDTO addSolutionReview(@RequestBody SolutionDTO solutionDTO) {
        return solutionService.addSolutionReview(solutionDTO);
    }

    @PutMapping("/modifiable")
    public SolutionDTO setModifiable(@RequestBody SolutionDTO solutionDTO) {
        return solutionService.setModifiable(solutionDTO);
    }

    @PutMapping("/{grade}")
    public SolutionDTO setGrade(@RequestBody SolutionDTO solutionDTO, @PathVariable String grade) {
        return solutionService.setGrade(solutionDTO, grade);
    }
}
