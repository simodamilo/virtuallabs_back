package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Team;
import it.polito.ai.virtuallabs_back.entities.VM;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.StudentRepository;
import it.polito.ai.virtuallabs_back.repositories.TeamRepository;
import it.polito.ai.virtuallabs_back.repositories.VMRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VMServiceImpl implements VMService {

    @Autowired
    UtilityService utilityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    VMRepository vmRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeamRepository teamRepository;


    @Override
    public List<VMDTO> getTeamVms(Long teamId) {
        Team team = utilityService.getTeam(teamId);

        if (utilityService.isTeacher()) {
            utilityService.courseOwnerValid(team.getCourse().getName());
        } else {
            if (!utilityService.getStudent().getTeams().contains(team))
                throw new TeamNotFoundException("The student is not part of the team");
        }

        return team.getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VMDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VMDTO> getCourseVms(String courseName) {
        utilityService.courseOwnerValid(courseName);

        return utilityService.getCourse(courseName)
                .getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VMDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VMDTO addVm(VMDTO vmDTO, Long teamId) {
        Team team = utilityService.getTeam(teamId);
        Student student = utilityService.getStudent();

        if (!student.getTeams().contains(team))
            throw new TeamNotFoundException("The student is not part of the team");

        if (!team.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        if (team.getCourse().getModelVM() == null)
            throw new VmChangeNotValidException("It is not possible to add a new VM");

        utilityService.constraintsCheck(vmDTO, teamId);

        if (team.getVms().size() >= team.getMaxInstance())
            throw new VmConstraintException("There are too many vm instances for this team");

        VM vm = vmRepository.save(modelMapper.map(vmDTO, VM.class));
        team.addVm(vm);
        vm.addOwner(student);
        team.getCourse().addVm(vm);

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public VMDTO modifyVm(VMDTO vmDTO) {
        VM vm = getAndCheck(vmDTO.getId());

        utilityService.constraintsCheck(vmDTO, vm.getTeam().getId());

        vm.setName(vmDTO.getName());
        vm.setVcpu(vmDTO.getVcpu());
        vm.setDisk(vmDTO.getDisk());
        vm.setRam(vmDTO.getRam());

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public VMDTO onOff(Long vmId) {
        VM vm = utilityService.getVm(vmId);

        if (utilityService.isTeacher()) {
            utilityService.courseOwnerValid(vm.getTeam().getCourse().getName());
        } else {
            if (!utilityService.getStudent().getTeams().contains(vm.getTeam()) || isNotValid(vmId))
                throw new TeamNotFoundException("You have no permission to modify this vm");
        }

        if (!vm.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        long alreadyActiveVms = vm.getTeam().getVms()
                .stream()
                .filter(VM::isActive)
                .count();

        System.out.println("Already active: " + alreadyActiveVms);

        if (!vm.isActive() && alreadyActiveVms >= vm.getTeam().getActiveInstance())
            throw new VmChangeNotValidException("There are too many active vms");

        vm.setActive(!vm.isActive());

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public VMDTO addOwner(Long id, String studentSerial) {
        VM vm = getAndCheck(id);

        if (!studentRepository.existsById(studentSerial))
            throw new StudentNotFoundException("Student not found");

        Student student = studentRepository.getOne(studentSerial);
        if (!vm.getTeam().getMembers().contains(student))
            throw new StudentNotFoundException("Student is not part of the team");

        if (!vm.addOwner(student))
            throw new StudentDuplicatedException("Student is already owner of the vm");

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public void deleteVm(Long id) {
        VM vm = getAndCheck(id);

        vm.getTeam().removeVm(vm);
        vm.getCourse().removeVm(vm);
        List<Student> owners = vm.getOwners();
        List<Student> toRemove = new ArrayList<>(owners);
        toRemove.forEach(vm::removeOwner);

        vmRepository.delete(vm);
    }


    private boolean isNotValid(Long vmId) {
        return utilityService.getStudent()
                .getVms()
                .stream()
                .noneMatch(vm -> vm.getId().equals(vmId));
    }

    private VM getAndCheck(Long vmId) {
        if (isNotValid(vmId))
            throw new VmChangeNotValidException("You have no permission to modify this vm");

        VM vm = utilityService.getVm(vmId);
        if (vm.isActive())
            throw new VmActiveException("Vm is on, so it is not possible to modify it");
        if (!vm.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        return vm;
    }
}
