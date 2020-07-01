package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface VMService {

    /**
     * This method is used to get a VM by using the id passed by the client. It returns an Optional,
     * so if the VM does not exist it will be empty.
     *
     * @param id it is the id of the searched vm.
     * @return the return is an optional, if vm does not exist it will be empty.
     */
    Optional<VMDTO> getVm(Long id);


    /**
     * This method is used to get all vms of the student by getting him/her from the principal.
     *
     * @return the method return all vms of the student, it could be empty.
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<VMDTO> getVmForStudent();


    /**
     * This method is used to get the vms of a team. The user is taken from the SecurityContextHolder and it is
     * used to get the corresponding student. Once I have the student, it is used to get the team with the
     * passed teamId and then all team's vms are taken.
     *
     * @param teamId we search all vms of the passed teamId.
     * @return the method return all vms of the student, it could be empty.
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<VMDTO> getVmForTeam(Long teamId);


    /**
     * This method his used to get all vms of the course.
     *
     * @param courseName we search all vms of the passed course.
     * @return the method return all vms of the student, it could be empty.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<VMDTO> getVmForCourse(String courseName);


    /**
     * This method is used to add a new vm, the id is created when the new vm is saved. The new vm is added
     * to the team and to the student.
     *
     * @param vmDTO  it is the vm that must be added.
     * @param teamId it is the the team in which we insert the vm.
     * @return it returns the inserted vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    VMDTO addVm(VMDTO vmDTO, Long teamId);


    /**
     * This method is used to modify the vm. The result is the new modified vm.
     *
     * @param vmDTO it is the vm that must be modified.
     * @return it returns the modified vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    VMDTO modifyVm(VMDTO vmDTO);


    /**
     * If all check are valid the status is changed by getting the negation of the previous value.
     *
     * @param id it is the id of the vm.
     * @return it returns the modified vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    VMDTO onOff(Long id);


    /**
     * With this method an owner is added to the vm. Some check are done in order to see if the user can
     * add the student as owner.
     *
     * @param id     it is the id of the vm.
     * @param serial it is the serial of the student that must be added as owner.
     * @return it returns the modified vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    VMDTO addOwner(Long id, String serial);


    /**
     * This method is used to delete a vm. It can be done if the student is one of the owner of the vm.
     *
     * @param id it is the id of the vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    void deleteVm(Long id);

}
