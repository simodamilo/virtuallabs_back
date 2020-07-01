package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import it.polito.ai.virtuallabs_back.services.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
    public SolutionDTO addSolution(@Valid @RequestBody SolutionDTO solutionDTO,
                                   @RequestParam(value = "imageFile") MultipartFile file,
                                   @PathVariable Long assignmentId) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        solutionDTO.setContent(file.getBytes());
        return solutionService.addSolution(solutionDTO, assignmentId);
    }

    @PostMapping("/review")
    public SolutionDTO addSolutionReview(@Valid @RequestBody SolutionDTO solutionDTO,
                                         @RequestParam(value = "imageFile") MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).split("/")[0].equals("image"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        solutionDTO.setContent(file.getBytes());
        return solutionService.addSolutionReview(solutionDTO);
    }

    @PutMapping("/modifiable")
    public SolutionDTO setModifiable(@Valid @RequestBody SolutionDTO solutionDTO) {
        return solutionService.setModifiable(solutionDTO);
    }

    @PutMapping("/{grade}")
    public SolutionDTO setGrade(@Valid @RequestBody SolutionDTO solutionDTO, @PathVariable String grade) {
        return solutionService.setGrade(solutionDTO, grade);
    }
}
