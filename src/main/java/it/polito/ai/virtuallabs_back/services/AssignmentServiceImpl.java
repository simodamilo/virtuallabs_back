package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs_back.entities.Assignment;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Solution;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.AssignmentChangeNotValid;
import it.polito.ai.virtuallabs_back.exception.AssignmentDateException;
import it.polito.ai.virtuallabs_back.exception.CourseNotEnabledException;
import it.polito.ai.virtuallabs_back.exception.ImageException;
import it.polito.ai.virtuallabs_back.repositories.AssignmentRepository;
import it.polito.ai.virtuallabs_back.repositories.SolutionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    UtilityService utilityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    SolutionRepository solutionRepository;

    @Override
    public byte[] getAssignmentContent(Long assignmentId) {
        return utilityService.getAssignment(assignmentId).getContent();
    }

    @Override
    public List<AssignmentDTO> getCourseAssignments(String courseName) {
        return utilityService.getCourse(courseName)
                .getAssignments()
                .stream()
                .map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, String courseName) {
        Teacher teacher = utilityService.getTeacher();
        Course course = utilityService.getCourse(courseName);

        if (!course.isEnabled())
            throw new CourseNotEnabledException("The course is not active");

        if (!teacher.getCourses().contains(course))
            throw new AssignmentChangeNotValid("You are not allowed to add an assignment to this course");

        if (assignmentDTO.getReleaseDate().after(assignmentDTO.getDeadline()))
            throw new AssignmentDateException("The deadline is before the release");

        Assignment assignment = assignmentRepository.save(modelMapper.map(assignmentDTO, Assignment.class));
        teacher.addAssignment(assignment);
        course.addAssignment(assignment);
        course.getStudents().forEach(
                student -> {
                    Solution s = Solution.builder()
                            .assignment(assignment)
                            .deliveryTs(new Timestamp(assignmentDTO.getReleaseDate().getTime()))
                            .state(Solution.State.NULL)
                            .student(student)
                            .modifiable(true)
                            .build();
                    solutionRepository.save(s);
                }
        );

        return modelMapper.map(assignment, AssignmentDTO.class);
    }

    @Override
    public AssignmentDTO addContent(Long assignmentId, MultipartFile file) {
        Teacher teacher = utilityService.getTeacher();
        Assignment assignment = utilityService.getAssignment(assignmentId);
        if (!assignment.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not active");

        if (!assignment.getTeacher().equals(teacher))
            throw new AssignmentChangeNotValid("You are not allowed to add an assignment to this course");
        try {
            assignment.setContent(file.getBytes());
        } catch (IOException e) {
            throw new ImageException("It is not possible to upload the image");
        }
        return modelMapper.map(assignment, AssignmentDTO.class);
    }

    @Override
    public void deleteAssignment(Long assignmentId) {
        Assignment assignment = utilityService.getAssignment(assignmentId);
        solutionRepository.deleteAllByAssignment(assignment);
        assignmentRepository.delete(assignment);
    }

    @Override
    @Scheduled(fixedRate = 1000 * 3600 * 24)
    public void assignmentExpired() {
        assignmentRepository.findAllByDeadlineBefore(new Date()).forEach(assignment -> {
            if (!assignment.isTerminated()) {
                assignment.getCourse().getStudents().forEach(student -> {
                    if (solutionRepository.getAllByStudentSerialAndAssignmentId(student.getSerial(), assignment.getId())
                            .stream()
                            .noneMatch(solution -> solution.getState().equals(Solution.State.DELIVERED))) {
                        solutionRepository.save(Solution.builder()
                                .modifiable(false)
                                .assignment(assignment)
                                .deliveryTs(new Timestamp(System.currentTimeMillis()))
                                .student(student)
                                .state(Solution.State.DELIVERED)
                                .build());
                    }
                });
                assignment.setTerminated(true);
            }
        });
    }
}
