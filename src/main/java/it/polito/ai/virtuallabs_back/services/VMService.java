package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface VMService {

    /**
     * Used to get the list of the vms of the team.
     *
     * @param teamId of the desired team.
     * @return list of found vms.
     */
    List<VMDTO> getTeamVms(Long teamId);

    /**
     * Used to get the list of the vms of the course.
     *
     * @param courseName of the desired course.
     * @return list of found vms.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<VMDTO> getCourseVms(String courseName);

    /**
     * Used by the student to add a vm.
     *
     * @param vmDTO  which needs to be added.
     * @param teamId in which the vm is added.
     * @return it returns the inserted vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    VMDTO addVm(VMDTO vmDTO, Long teamId);

    /**
     * Used by the student to modify a vm.
     *
     * @param vmDTO which needs to be modified.
     * @return the modified vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    VMDTO modifyVm(VMDTO vmDTO);

    /**
     * Used to turn on/off a vm.
     *
     * @param vmId which needs to be turned on/off.
     * @return the turned on/off vm.
     */
    VMDTO onOff(Long vmId);

    /**
     * Used by the student to add an owner to the vm.
     *
     * @param vmId          in which the owner is added.
     * @param studentSerial which needs to be added.
     * @return the modified vm.
     */
    @PreAuthorize("hasRole('STUDENT')")
    VMDTO addOwner(Long vmId, String studentSerial);

    /**
     * Used by the student to delete a vm.
     *
     * @param vmId which needs to be deleted.
     */
    @PreAuthorize("hasRole('STUDENT')")
    void deleteVm(Long vmId);

}
