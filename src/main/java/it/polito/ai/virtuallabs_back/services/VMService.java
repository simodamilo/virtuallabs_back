package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface VMService {

    /**
     * This method is used to get a VM by using the id passed by the client. It returns an Optional,
     * so if the VM does not exist it will be empty.
     */
    Optional<VMDTO> getVm(Long id);


    /**
     * This method is used to fet the vms of a team. The user is taken from the SecurityContextHolder and it is
     * used to get the corresponding student. Once I have the student, it is used to get the team with the
     * passed courseName and then all team's vms are taken.
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<VMDTO> getVMForTeam(String courseName);


    /**
     * This method is used to add a new vm, the id is created when the new vm is saved.
     */
    @PreAuthorize("hasRole('STUDENT')")
    boolean addVm(VMDTO vm, String courseName);


    @PreAuthorize("hasRole('STUDENT')")
    VMDTO modifyVm(VMDTO vmdto);


    /**
     * This method is used to delete a vm. It can be done if the student is one of the owner of the vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    void deleteVm(Long id);

}
