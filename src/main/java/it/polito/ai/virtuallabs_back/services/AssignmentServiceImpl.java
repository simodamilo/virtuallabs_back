package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs_back.entities.Assignment;
import it.polito.ai.virtuallabs_back.repositories.AssignmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Optional<AssignmentDTO> getAssignment(Long id) {
        return assignmentRepository.findById(id).map(a -> modelMapper.map(a, AssignmentDTO.class));
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByCourse(String courseName) {

        return assignmentRepository.findAllByCourseName(courseName)
                .stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addAssignment(AssignmentDTO assignmentDTO) {
        assignmentRepository.save(modelMapper.map(assignmentDTO, Assignment.class));
        return true;
    }
}
