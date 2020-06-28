package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;

import java.util.List;
import java.util.Optional;

public interface SolutionService {

    Optional<SolutionDTO> getSolution(Long id);

    List<SolutionDTO> getSolutionsByAssignment(Long id);

    boolean addSolution(SolutionDTO solutionDTO);

}
