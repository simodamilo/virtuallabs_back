package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.entities.Team;
import it.polito.ai.virtuallabs_back.entities.VM;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class VMServiceImpl implements VMService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    VMRepository vmRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TeacherRepository teacherRepository;


    @Override
    public Optional<VMDTO> getVm(Long id) {
        return vmRepository.findById(id)
                .map(v -> modelMapper.map(v, VMDTO.class));
    }

    @Override
    public List<VMDTO> getVmForStudent() {
        Student student = getStudentFromPrincipal();

        return student
                .getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VMDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VMDTO> getVmForTeam(Long teamId) {
        if (!teamRepository.existsById(teamId))
            throw new TeamNotFoundException("Team does not exist");

        Team team = teamRepository.getOne(teamId);
        Student student = getStudentFromPrincipal();

        if (!student.getTeams().contains(team))
            throw new TeamNotFoundException("Team does not exist"); // fare nuova eccezione?

        return teamRepository.getOne(teamId)
                .getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VMDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VMDTO> getVmForCourse(String courseName) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher teacher = teacherRepository.getOne(principal.getUsername().split("@")[0]);

        if (!teacher.getCourses().contains(courseRepository.getOne(courseName)))
            throw new CourseNotFoundException("Course does not exist"); // fare nuova eccezione?

        return courseRepository.getOne(courseName)
                .getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VMDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VMDTO addVm(VMDTO vmDTO, Long teamId) {
        //TODO numero di vm già create
        if (!teamRepository.existsById(teamId))
            throw new TeamNotFoundException("Team does not exist");

        Team team = teamRepository.getOne(teamId);
        Student student = getStudentFromPrincipal();

        if (!student.getTeams().contains(team))
            throw new TeamNotFoundException("Team does not exist"); // aggiungere una nuova eccezione?

        if (!team.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        if (team.getModelVM() == null)
            throw new VmChangeNotValidException("It is not possible to add a new VM");

        constraintsCheck(vmDTO, teamId); /* check related to vcpu, ram and disk */

        VM vm = vmRepository.save(modelMapper.map(vmDTO, VM.class));
        team.addVm(vm); /* add vm to team */
        vm.addOwner(student); /* add vm to student */
        team.getCourse().addVm(vm); /* add vm to course */

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public VMDTO modifyVm(VMDTO vmDTO) {
        VM vm = getAndCheck(vmDTO.getId());

        constraintsCheck(vmDTO, vm.getTeam().getId()); /* check related to vcpu, ram and disk */

        vm.setVcpu(vmDTO.getVcpu());
        vm.setDisk(vmDTO.getDisk());
        vm.setRam(vmDTO.getRam());

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public VMDTO onOff(Long id) {
        //TODO numero di vm già attive
        if (!vmRepository.existsById(id))
            throw new VmNotFoundException("Vm not found");
        if (isNotValid(id))
            throw new VmChangeNotValidException("You have no permission to modify this vm");

        VM vm = vmRepository.getOne(id);
        if (!vm.getTeam().getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        vm.setActive(!vm.isActive());

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public VMDTO addOwner(Long id, String serial) {
        VM vm = getAndCheck(id);

        Student studentToAdd = studentRepository.getOne(serial);
        if (!vm.getTeam().getMembers().contains(studentToAdd))
            throw new StudentNotFoundException("Student is not part of the team"); // vedere se inserire un'altra eccezione

        if (!vm.addOwner(studentToAdd))
            throw new StudentDuplicatedException("Student is already owner of the vm"); // vedere se inserire un'altra eccezione

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public void deleteVm(Long id) {
        VM vm = getAndCheck(id);

        vm.getTeam().removeVm(vm); /* delete vm from team */
        vm.getCourse().removeVm(vm); /* delete vm from course */
        List<Student> owners = vm.getOwners(); /* delete vm from owners */
        List<Student> toRemove = new ArrayList<>(owners);
        toRemove.forEach(vm::removeOwner);

        vmRepository.delete(vm);
    }


    private boolean isNotValid(Long id) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername().split("@")[0]);

        VM vm = student.getVms()
                .stream()
                .filter(v -> v.getId().equals(id))
                .findAny()
                .orElse(null);

        return vm == null;
    }

    private Student getStudentFromPrincipal() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentRepository.getOne(principal.getUsername().split("@")[0]);
    }

    private void constraintsCheck(VMDTO vmDTO, Long teamId) {
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

    private VM getAndCheck(Long vmId) {
        if (!vmRepository.existsById(vmId))
            throw new VmNotFoundException("Vm not found");
        if (isNotValid(vmId))
            throw new VmChangeNotValidException("You have no permission to modify this vm");

        VM vm = vmRepository.getOne(vmId);
        if (vm.isActive())
            throw new VmActiveException("Vm is on, so it is not possible to modify it");
        if (!vm.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        return vm;
    }
}
