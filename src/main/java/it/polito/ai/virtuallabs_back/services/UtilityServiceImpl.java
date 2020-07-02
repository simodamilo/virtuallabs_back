package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.entities.*;
import it.polito.ai.virtuallabs_back.exception.CourseNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UtilityServiceImpl implements UtilityService {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ModelVMRepository modelVMRepository;

    @Autowired
    SolutionRepository solutionRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    VMRepository vmRepository;

    @Override
    public Assignment getAssignment(Long assignmentId) {
        if (!assignmentRepository.existsById(assignmentId))
            throw new CourseNotFoundException("Assignment not found");
        return assignmentRepository.getOne(assignmentId);
    }

    @Override
    public Course getCourse(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");
        return courseRepository.getOne(courseName);
    }

    @Override
    public ModelVM getModelVm(Long modelVmId) {
        if (!modelVMRepository.existsById(modelVmId))
            throw new CourseNotFoundException("modelVm not found");
        return modelVMRepository.getOne(modelVmId);
    }

    @Override
    public Solution getSolution(Long solutionId) {
        if (!solutionRepository.existsById(solutionId))
            throw new CourseNotFoundException("Solution not found");
        return solutionRepository.getOne(solutionId);
    }

    @Override
    public Student getStudent() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentRepository.getOne(principal.getUsername().split("@")[0]);
    }

    @Override
    public Teacher getTeacher() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return teacherRepository.getOne(principal.getUsername().split("@")[0]);
    }

    @Override
    public Team getTeam(Long teamId) {
        if (!teamRepository.existsById(teamId))
            throw new CourseNotFoundException("Team not found");
        return teamRepository.getOne(teamId);
    }

    @Override
    public VM getVm(Long vmId) {
        if (!vmRepository.existsById(vmId))
            throw new CourseNotFoundException("Vm not found");
        return vmRepository.getOne(vmId);
    }
}
