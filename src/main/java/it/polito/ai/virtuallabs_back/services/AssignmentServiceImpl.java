package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs_back.entities.Assignment;
import it.polito.ai.virtuallabs_back.exception.AssignmentNotFoundException;
import it.polito.ai.virtuallabs_back.exception.CourseNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.AssignmentRepository;
import it.polito.ai.virtuallabs_back.repositories.CourseRepository;
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
    CourseRepository courseRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Optional<AssignmentDTO> getAssignment(Long id) {
        return assignmentRepository.findById(id).map(a -> modelMapper.map(a, AssignmentDTO.class));
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByCourse(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");
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


    @Override
    public boolean modifyAssignment(AssignmentDTO assignmentDTO) {
        if (!assignmentRepository.existsById(assignmentDTO.getId()))
            throw new AssignmentNotFoundException("Assignment not found");
        Assignment a = assignmentRepository.getOne(assignmentDTO.getId());
        a.setDeadline(assignmentDTO.getDeadline());
        a.setContent(assignmentDTO.getContent());
        return true;
    }
}
