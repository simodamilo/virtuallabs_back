package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import it.polito.ai.virtuallabs_back.entities.*;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.atomic.AtomicInteger;

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
            throw new AssignmentNotFoundException("The assignment you are looking for does not exist");
        return assignmentRepository.getOne(assignmentId);
    }

    @Override
    public Course getCourse(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("The course you are looking for does not exist");
        return courseRepository.getOne(courseName);
    }

    @Override
    public ModelVM getModelVm(Long modelVmId) {
        if (!modelVMRepository.existsById(modelVmId))
            throw new ModelVMNotFoundException("The VM model you are looking for does not exist");
        return modelVMRepository.getOne(modelVmId);
    }

    @Override
    public Solution getSolution(Long solutionId) {
        if (!solutionRepository.existsById(solutionId))
            throw new SolutionNotFoundException("The solution you are looking for does not exist");
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
            throw new TeamNotFoundException("The team you are looking for does not exist");
        return teamRepository.getOne(teamId);
    }

    @Override
    public VM getVm(Long vmId) {
        if (!vmRepository.existsById(vmId))
            throw new VmNotFoundException("The VM you are looking for does not exist");
        return vmRepository.getOne(vmId);
    }

    @Override
    public void courseOwnerValid(String courseName) {
        if (!getTeacher().getCourses().contains(getCourse(courseName)))
            throw new CourseChangeNotValidException("You are not allowed to edit this course");
    }

    @Override
    public void constraintsCheck(VMDTO vmDTO, Long teamId) {
        AtomicInteger disk = new AtomicInteger(vmDTO.getDisk());
        AtomicInteger vcpu = new AtomicInteger(vmDTO.getVcpu());
        AtomicInteger ram = new AtomicInteger(vmDTO.getRam());

        Team team = teamRepository.getOne(teamId);
        team.getVms().forEach(vm -> {
            if (vmDTO.getId() != null) {
                if (!vmDTO.getId().equals(vm.getId())) {
                    disk.set(disk.get() + vm.getDisk());
                    vcpu.set(vcpu.get() + vm.getVcpu());
                    ram.set(ram.get() + vm.getRam());
                }
            } else {
                disk.set(disk.get() + vm.getDisk());
                vcpu.set(vcpu.get() + vm.getVcpu());
                ram.set(ram.get() + vm.getRam());
            }
        });

        if (disk.get() > team.getDisk() || vcpu.get() > team.getVcpu() || ram.get() > team.getRam())
            throw new VmConstraintException("The new vm does not respect the team constraints");
    }

    @Override
    public boolean isTeacher() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_TEACHER"));
    }
}
