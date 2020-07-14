package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import it.polito.ai.virtuallabs_back.entities.*;
import it.polito.ai.virtuallabs_back.exception.CourseNotEnabledException;
import it.polito.ai.virtuallabs_back.exception.CourseNotValidException;
import it.polito.ai.virtuallabs_back.exception.SolutionChangeNotValid;
import it.polito.ai.virtuallabs_back.exception.StudentNotEnrolledException;
import it.polito.ai.virtuallabs_back.repositories.SolutionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    UtilityService utilityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SolutionRepository solutionRepository;

    @Override
    public Optional<SolutionDTO> getSolution(Long solutionId) {
        return solutionRepository.findById(solutionId).map(solution -> modelMapper.map(solution, SolutionDTO.class));
    }

    @Override
    public List<SolutionDTO> getAssignmentSolutions(Long assignmentId) {
        Teacher teacher = utilityService.getTeacher();
        Assignment assignment = utilityService.getAssignment(assignmentId);

        if (!teacher.getCourses().contains(assignment.getCourse()))
            throw new CourseNotValidException("You have no permission to see the solution of this course");

        return assignment.getSolutions()
                .stream()
                .map(solution -> modelMapper.map(solution, SolutionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SolutionDTO> getCourseSolutions(String courseName) {
        List<SolutionDTO> list = new ArrayList<>();
        utilityService.getCourse(courseName)
                .getAssignments()
                .forEach(assignment -> assignment.getSolutions()
                        .forEach(solution -> list.add(modelMapper.map(solution, SolutionDTO.class))
                        ));
        return list;
    }


    @Override
    public List<SolutionDTO> getStudentSolutions(String courseName) {
        Student student = utilityService.getStudent();
        Course course = utilityService.getCourse(courseName);

        if (!student.getCourses().contains(course))
            throw new StudentNotEnrolledException("You are not enrolled in the course");

        List<SolutionDTO> list = new ArrayList<>();
        course.getAssignments().forEach(assignment ->
                solutionRepository.getAllByStudentAndAssignment(student, assignment)
                        .forEach(solution -> list.add(modelMapper.map(solution, SolutionDTO.class)))
        );

        return list;
    }

    @Override
    public SolutionDTO addSolution(SolutionDTO solutionDTO, Long assignmentId) {
        if (solutionDTO.getState().equals(Solution.State.NULL) || solutionDTO.getState().equals(Solution.State.REVIEWED))
            throw new SolutionChangeNotValid("Impossible to change this solution");

        Assignment assignment = utilityService.getAssignment(assignmentId);
        Student student = utilityService.getStudent();

        if (solutionRepository.getAllByStudentAndAssignment(student, assignment)
                .stream().anyMatch(solution -> !solution.isModifiable()))
            throw new SolutionChangeNotValid("Impossible to change this solution");

        Solution solution = solutionRepository.save(modelMapper.map(solutionDTO, Solution.class));
        student.addSolution(solution);
        assignment.addSolution(solution);

        return modelMapper.map(solution, SolutionDTO.class);
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
        Solution solution = isValid(solutionDTO);
        solution.setModifiable(solutionDTO.isModifiable());

        return modelMapper.map(solution, SolutionDTO.class);
    }

    @Override
    public SolutionDTO setGrade(SolutionDTO solutionDTO, String vote) {
        Solution solution = isValid(solutionDTO);
        solution.setGrade(vote);

        return modelMapper.map(solution, SolutionDTO.class);
    }

    private Solution isValid(SolutionDTO solutionDTO) {
        Teacher teacher = utilityService.getTeacher();
        Solution solution = utilityService.getSolution(solutionDTO.getId());
        Course course = utilityService.getCourse(solution.getAssignment().getCourse().getName());

        if (!course.isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        if (!teacher.getCourses().contains(solution.getAssignment().getCourse()))
            throw new SolutionChangeNotValid("You have no permission to change this Solution");

        return solution;
    }
}
