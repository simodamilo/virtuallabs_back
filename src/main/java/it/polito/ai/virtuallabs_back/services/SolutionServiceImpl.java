package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import it.polito.ai.virtuallabs_back.entities.Solution;
import it.polito.ai.virtuallabs_back.repositories.SolutionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SolutionServiceImpl implements SolutionService {

    @Autowired
    SolutionRepository solutionRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Optional<SolutionDTO> getSolution(Long id) {
        return solutionRepository.findById(id).map(s -> modelMapper.map(s, SolutionDTO.class));
    }

    @Override
    public List<SolutionDTO> getSolutionsByAssignment(Long id) {
        return solutionRepository.findAllByAssignmentId(id)
                .stream()
                .map(s -> modelMapper.map(s, SolutionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addSolution(SolutionDTO solutionDTO) {
        solutionRepository.save(modelMapper.map(solutionDTO, Solution.class));
        return true;
    }
}
