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

    @PostMapping({"", "/"})
    public SolutionDTO addSolution(@RequestBody SolutionDTO dto) {
        if (!solutionService.addSolution(dto))
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.getId().toString());
        return dto;
    }
}
