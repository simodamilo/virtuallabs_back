package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import it.polito.ai.virtuallabs_back.entities.*;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SolutionServiceImpl implements SolutionService {

    @Autowired
    SolutionRepository solutionRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Optional<SolutionDTO> getSolution(Long id) {
        return solutionRepository.findById(id).map(s -> modelMapper.map(s, SolutionDTO.class));
    }

    @Override
    public List<SolutionDTO> getAssignmentSolutions(Long assignmentId) {
        if (!assignmentRepository.existsById(assignmentId))
            throw new AssignmentNotFoundException("Assignment not found");
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher t = teacherRepository.getOne(principal.getUsername().split("@")[0]);
        Assignment a = assignmentRepository.getOne(assignmentId);
        if (!t.getCourses().contains(a.getCourse()))
            throw new CourseNotValidException("You have no permission to see the solution of this course");
        return assignmentRepository.getOne(assignmentId)
                .getSolutions()
                .stream()
                .map(s -> modelMapper.map(s, SolutionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SolutionDTO> getStudentSolutions(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student s = studentRepository.getOne(principal.getUsername().split("@")[0]);
        Course c = courseRepository.getOne(courseName);
        if (!s.getCourses().contains(c))
            throw new StudentNotEnrolledException("You are not enrolled in the course");
        List<SolutionDTO> list = new ArrayList<>();
        courseRepository.getOne(courseName).getAssignments().forEach(assignment ->
                solutionRepository.getAllByStudentAndAssignment(s, assignment)
                        .forEach(solution -> list.add(modelMapper.map(solution, SolutionDTO.class)))
        );
        return list;
    }

    @Override
    public SolutionDTO addSolution(SolutionDTO solutionDTO, Long assignmentId) {
        if (!assignmentRepository.existsById(assignmentId))
            throw new AssignmentNotFoundException("Assignment not found");
        if (solutionDTO.getState().equals(Solution.State.NULL) || solutionDTO.getState().equals(Solution.State.REVIEWED))
            throw new SolutionChangeNotValid("Impossible to change this solution");
        Assignment assignment = assignmentRepository.getOne(assignmentId);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername().split("@")[0]);
        if (solutionRepository.getByStudentSerialAndAssignmentAndModifiableFalse(student.getSerial(), assignment) != null)
            throw new SolutionChangeNotValid("Impossible to change this solution");
        Solution s = solutionRepository.save(modelMapper.map(solutionDTO, Solution.class));
        student.addSolution(s);
        assignment.addSolution(s);
        return modelMapper.map(s, SolutionDTO.class);
    }

    @Override
    public SolutionDTO addSolutionReview(SolutionDTO solutionDTO) {
        Solution s = isValid(solutionDTO);
        solutionDTO.setState(Solution.State.REVIEWED);
        solutionDTO.setId(null);
        Solution solution = solutionRepository.save(modelMapper.map(solutionDTO, Solution.class));
        solution.setStudent(s.getStudent());
        solution.setAssignment(s.getAssignment());
        return modelMapper.map(solution, SolutionDTO.class);
    }

    @Override
    public SolutionDTO setModifiable(SolutionDTO solutionDTO) {
        Solution s = isValid(solutionDTO);
        s.setModifiable(solutionDTO.isModifiable());
        return modelMapper.map(s, SolutionDTO.class);
    }

    @Override
    public SolutionDTO setGrade(SolutionDTO solutionDTO, String vote) {
        Solution s = isValid(solutionDTO);
        s.setGrade(vote);
        return modelMapper.map(s, SolutionDTO.class);
    }

    private Solution isValid(SolutionDTO solutionDTO) {
        if (!solutionRepository.existsById(solutionDTO.getId()))
            throw new SolutionNotFoundException("Solution not found");
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher t = teacherRepository.getOne(principal.getUsername().split("@")[0]);
        Solution s = solutionRepository.getOne(solutionDTO.getId());
        if (!t.getCourses().contains(s.getAssignment().getCourse()))
            throw new SolutionChangeNotValid("You have no permission to change this Solution");
        return s;
    }
}
