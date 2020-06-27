package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Team;
import it.polito.ai.virtuallabs_back.entities.VM;
import it.polito.ai.virtuallabs_back.exception.TeamNotFoundException;
import it.polito.ai.virtuallabs_back.exception.VmChangeNotValidException;
import it.polito.ai.virtuallabs_back.exception.VmNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.StudentRepository;
import it.polito.ai.virtuallabs_back.repositories.TeamRepository;
import it.polito.ai.virtuallabs_back.repositories.VMRepository;
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
public class VMServiceImpl implements VMService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    VMRepository vmRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeamRepository teamRepository;


    @Override
    public Optional<VMDTO> getVm(Long id) { /* non so se serve */
        return vmRepository.findById(id)
                .map(v -> modelMapper.map(v, VMDTO.class));
    }

    @Override
    public List<VMDTO> getVMForTeam(String courseName) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername());
        // bisogna fare controlli sullo studente?

        Team team = student.getTeams()
                .stream()
                .filter(t -> t.getCourse().getName().equals(courseName))
                .findAny()
                .orElse(null);

        if (team == null) return new ArrayList<>();


        return team.getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VMDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public boolean addVm(VMDTO vmDTO, String courseName) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername());

        Team team = student.getTeams()
                .stream()
                .filter(t -> t.getCourse().getName().equals(courseName))
                .findAny()
                .orElse(null);
        if (team == null) throw new TeamNotFoundException("Team does not exist");

        VM vm = vmRepository.save(modelMapper.map(vmDTO, VM.class));
        team.addVm(vm); // aggiungo la vm al team
        vm.addOwner(student); //aggiungo la vm allo studente

        return true;
    }

    @Override
    public VMDTO modifyVm(VMDTO vmdto) {
        if (!vmRepository.existsById(vmdto.getId()))
            throw new VmNotFoundException("Vm not found");

        if (isNotValid(vmdto.getId()))
            throw new VmChangeNotValidException("You have no permission to delete this vm");

        VM vm = vmRepository.getOne(vmdto.getId());
        vm.setVcpu(vmdto.getVcpu());
        vm.setDisk(vmdto.getDisk());
        vm.setRam(vmdto.getRam());

        return modelMapper.map(vm, VMDTO.class);
    }

    @Override
    public void deleteVm(Long id) {
        if (!vmRepository.existsById(id))
            throw new VmNotFoundException("Vm not found");

        if (isNotValid(id))
            throw new VmChangeNotValidException("You have no permission to delete this vm");

        VM vm = vmRepository.getOne(id);
        vm.getTeam().getVms().remove(vm); // rimuovo la vm dal team

        List<Student> owners = vm.getOwners(); // rimuovo la vm dagli owners
        List<Student> toRemove = new ArrayList<>(owners);
        toRemove.forEach(vm::removeOwner);

        vmRepository.delete(vm);
    }


    private boolean isNotValid(Long id) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername());

        VM vm = student.getVms()
                .stream()
                .filter(v -> v.getId().equals(id))
                .findAny()
                .orElse(null);

        return vm == null;
    }

    /*private Team getTeamFromPrincipal(String courseName){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername());

        return student.getTeams()
                .stream()
                .filter(t -> t.getCourse().getName().equals(courseName))
                .findAny()
                .orElse(null);
    }*/

}
