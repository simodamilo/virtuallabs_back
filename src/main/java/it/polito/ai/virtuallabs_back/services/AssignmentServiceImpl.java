package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs_back.entities.Assignment;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Solution;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.AssignmentChangeNotValid;
import it.polito.ai.virtuallabs_back.exception.AssignmentDateException;
import it.polito.ai.virtuallabs_back.exception.AssignmentNotFoundException;
import it.polito.ai.virtuallabs_back.exception.CourseNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
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
    TeacherRepository teacherRepository;

    @Autowired
    SolutionRepository solutionRepository;

    @Autowired
    StudentRepository studentRepository;

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
        return courseRepository.getOne(courseName)
                .getAssignments()
                .stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");
        if (!assignmentDTO.getReleaseDate().after(assignmentDTO.getDeadline()))
            throw new AssignmentDateException("Deadline before release");
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assignment a = assignmentRepository.save(modelMapper.map(assignmentDTO, Assignment.class));
        Teacher t = teacherRepository.getOne(principal.getUsername().split("@")[0]);
        Course c = courseRepository.getOne(courseName);
        if (!t.getCourses().contains(c))
            throw new AssignmentChangeNotValid("You have no permission to add an assignment to this course");
        t.addAssignment(a);
        c.addAssignment(a);
        courseRepository.getOne(courseName).getStudents().forEach(
                student -> {
                    Solution s = Solution.builder()
                            .assignment(a)
                            .content(null)
                            .deliveryTs(new Timestamp(assignmentDTO.getReleaseDate().getTime()))
                            .state(Solution.State.NULL)
                            .student(student)
                            .active(true)
                            .build();
                    solutionRepository.save(s);
                }
        );
        return modelMapper.map(a, AssignmentDTO.class);
    }

    @Override
    public AssignmentDTO modifyAssignment(AssignmentDTO assignmentDTO) {
        if (!assignmentRepository.existsById(assignmentDTO.getId()))
            throw new AssignmentNotFoundException("Assignment not found");
        if (!assignmentDTO.getReleaseDate().after(assignmentDTO.getDeadline()))
            throw new AssignmentDateException("Deadline before release");
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher t = teacherRepository.getOne(principal.getUsername().split("@")[0]);
        Assignment a = assignmentRepository.getOne(assignmentDTO.getId());
        if (!a.getTeacher().equals(t))
            throw new AssignmentChangeNotValid("You have no permission to add an assignment to this course");
        a.setDeadline(assignmentDTO.getDeadline());
        a.setReleaseDate(assignmentDTO.getReleaseDate());
        a.setContent(assignmentDTO.getContent());
        return assignmentDTO;
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void assignmentExpired() {
        assignmentRepository.findAllByDeadlineBefore(new Date()).forEach(a -> {
            if (!a.isTerminated()) {
                a.getCourse().getStudents().forEach(student -> {
                    if (solutionRepository.getAllByStudentAndAssignment(student, a)
                            .stream()
                            .noneMatch(solution -> solution.getState().equals(Solution.State.DELIVERED))) {
                        solutionRepository.save(Solution.builder()
                                .active(false)
                                .assignment(a)
                                .content(null)
                                .deliveryTs(new Timestamp(System.currentTimeMillis()))
                                .student(student)
                                .state(Solution.State.DELIVERED)
                                .build());
                    }
                });
                a.setTerminated(true);
            }
        });

    }

}
