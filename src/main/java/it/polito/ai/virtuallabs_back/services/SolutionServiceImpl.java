package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.SolutionDTO;
import it.polito.ai.virtuallabs_back.entities.Assignment;
import it.polito.ai.virtuallabs_back.entities.Solution;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.AssignmentChangeNotValid;
import it.polito.ai.virtuallabs_back.exception.CourseNotEnabledException;
import it.polito.ai.virtuallabs_back.exception.CourseNotValidException;
import it.polito.ai.virtuallabs_back.exception.SolutionChangeNotValid;
import it.polito.ai.virtuallabs_back.repositories.SolutionRepository;
import it.polito.ai.virtuallabs_back.repositories.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    StudentRepository studentRepository;

    @Override
    public byte[] getSolutionContent(Long solutionId) {
        return utilityService.getSolution(solutionId).getContent();
    }

    @Override
    public List<SolutionDTO> getAssignmentSolutions(Long assignmentId) {
        Teacher teacher = utilityService.getTeacher();
        Assignment assignment = utilityService.getAssignment(assignmentId);

        if (!teacher.getCourses().contains(assignment.getCourse()))
            throw new CourseNotValidException("You have no permission to see the solution of this course");

        HashMap<String, SolutionDTO> map = new HashMap<>();
        solutionRepository.getAllByAssignmentStudentAndMaxTs(assignmentId)
                .forEach(solution -> {
                    if (!map.containsKey(solution.getStudent().getSerial()))
                        map.put(solution.getStudent().getSerial(), modelMapper.map(solution, SolutionDTO.class));
                    else if (solution.getDeliveryTs().after(map.get(solution.getStudent().getSerial()).getDeliveryTs()))
                        map.replace(solution.getStudent().getSerial(), modelMapper.map(solution, SolutionDTO.class));
                });
        return new ArrayList<>(map.values());
    }

    @Override
    public List<SolutionDTO> getStudentSolutions(Long assignmentId, String studentSerial) {
        Assignment assignment = utilityService.getAssignment(assignmentId);
        if (utilityService.isTeacher()) {
            utilityService.courseOwnerValid(assignment.getCourse().getName());
        } else {
            if (!studentSerial.equals(utilityService.getStudent().getSerial()))
                throw new SolutionChangeNotValid("you are not allowed to change this solution");
        }
        return assignment.getSolutions()
                .stream()
                .filter(solution -> solution.getStudent().getSerial().equals(studentSerial))
                .map(solution -> modelMapper.map(solution, SolutionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public SolutionDTO addSolution(SolutionDTO solutionDTO, Long assignmentId) {
        if (solutionDTO.getState().equals(Solution.State.NULL) || solutionDTO.getState().equals(Solution.State.REVIEWED))
            throw new SolutionChangeNotValid("Impossible to change this solution");

        Assignment assignment = utilityService.getAssignment(assignmentId);
        Student student = utilityService.getStudent();

        if (solutionRepository.getAllByStudentSerialAndAssignmentId(student.getSerial(), assignment.getId())
                .stream().anyMatch(solution -> !solution.isModifiable()))
            throw new SolutionChangeNotValid("Impossible to change this solution");

        Solution solution = solutionRepository.save(modelMapper.map(solutionDTO, Solution.class));
        student.addSolution(solution);
        assignment.addSolution(solution);

        return modelMapper.map(solution, SolutionDTO.class);
    }

    @Override
    public SolutionDTO addSolutionReview(SolutionDTO solutionDTO, Long assignmentId, String studentSerial) {
        if (!solutionDTO.getState().equals(Solution.State.REVIEWED))
            throw new SolutionChangeNotValid("Impossible to change this solution");

        Assignment assignment = utilityService.getAssignment(assignmentId);

        utilityService.courseOwnerValid(assignment.getCourse().getName());

        Solution solution = solutionRepository.save(modelMapper.map(solutionDTO, Solution.class));
        studentRepository.getOne(studentSerial).addSolution(solution);
        assignment.addSolution(solution);

        if (!solutionDTO.isModifiable())
            solutionRepository.getAllByStudentSerialAndAssignmentId(studentSerial, assignmentId)
                    .forEach(solution1 -> solution1.setModifiable(false));


        return modelMapper.map(solution, SolutionDTO.class);
    }

    @Override
    public SolutionDTO addContent(Long solutionId, MultipartFile file) {
        Solution solution = utilityService.getSolution(solutionId);
        if (!solution.getAssignment().getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        if (utilityService.isTeacher()) {
            Teacher teacher = utilityService.getTeacher();
            if (!solution.getAssignment().getTeacher().equals(teacher))
                throw new AssignmentChangeNotValid("You have no permission to modify an assignment to this course");
        }

        try {
            solution.setContent(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return modelMapper.map(solution, SolutionDTO.class);
    }

    @Override
    public void deleteSolution(Long solutionId) {
        solutionRepository.delete(utilityService.getSolution(solutionId));
    }
}
