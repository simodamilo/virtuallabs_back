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

    @GetMapping("/{id}")
    public SolutionDTO getOne(@PathVariable Long id) {
        if (!solutionService.getSolution(id).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, id.toString());
        return solutionService.getSolution(id).get();
    }

    @GetMapping("/assignments/{id}")
    public List<SolutionDTO> allByAssignment(@PathVariable Long id) {
        return solutionService.getSolutionsByAssignment(id);
    }

    @GetMapping("/courses/{courseName}")
    public List<SolutionDTO> allByStudent(@PathVariable String courseName) {
        return solutionService.getSolutionsByStudent(courseName);
    }

    @PostMapping("/{assignmentId}")
    public SolutionDTO addSolution(@RequestBody SolutionDTO dto, @PathVariable Long assignmentId) {
        return solutionService.addSolution(dto, assignmentId);
    }

    @PostMapping("/review")
    public SolutionDTO addReview(@RequestBody SolutionDTO dto) {
        return solutionService.reviewSolution(dto);
    }

    @PutMapping("/active")
    public SolutionDTO setActive(@RequestBody SolutionDTO dto) {
        return solutionService.setActive(dto);
    }

    @PutMapping("/{vote}")
    public SolutionDTO addGrade(@RequestBody SolutionDTO dto, @PathVariable String vote) {
        return solutionService.setVote(dto, vote);
    }
}
