package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import it.polito.ai.virtuallabs_back.services.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/API/solutions")
public class SolutionController {

    @Autowired
    SolutionService solutionService;

    @GetMapping("/{solutionId}")
    public byte[] getAssignmentContent(@PathVariable Long solutionId) {
        return solutionService.getSolutionContent(solutionId);
    }

    @GetMapping("/assignments/{assignmentId}")
    public List<SolutionDTO> getAssignmentSolutions(@PathVariable Long assignmentId) {
        return solutionService.getAssignmentSolutions(assignmentId);
    }

    @GetMapping("/assignments/{assignmentId}/students/{studentSerial}")
    public List<SolutionDTO> getStudentSolutions(@PathVariable Long assignmentId, @PathVariable String studentSerial) {
        return solutionService.getStudentSolutions(assignmentId, studentSerial);
    }

    @PostMapping("/{assignmentId}")
    public SolutionDTO addSolution(@Valid @RequestBody SolutionDTO solutionDTO,
                                   @PathVariable Long assignmentId) {
        return solutionService.addSolution(solutionDTO, assignmentId);
    }

    @PostMapping("/{assignmentId}/{studentSerial}")
    public SolutionDTO addSolutionReview(@Valid @RequestBody SolutionDTO solutionDTO, @PathVariable Long assignmentId, @PathVariable String studentSerial) {
        return solutionService.addSolutionReview(solutionDTO, assignmentId, studentSerial);
    }

    @PutMapping("/{solutionId}")
    public SolutionDTO addContent(@PathVariable Long solutionId, @RequestParam(value = "imageFile") MultipartFile file) {
        return solutionService.addContent(solutionId, file);
    }

}
