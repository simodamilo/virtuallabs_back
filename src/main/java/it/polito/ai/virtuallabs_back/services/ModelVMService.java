package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ModelVMService {

    /**
     * Used by the student to add a modelVM.
     *
     * @param modelVMDTO which needs to be added.
     * @param courseName in which the modelVM is added.
     * @return the added modelVM.
     */
    @PreAuthorize("hasRole('TEACHER')")
    ModelVMDTO addModelVm(ModelVMDTO modelVMDTO, String courseName);

    /**
     * Used by the student to modify a modelVM.
     *
     * @param modelVMDTO which needs to be modified.
     * @return the modified modelVM.
     */
    @PreAuthorize("hasRole('TEACHER')")
    ModelVMDTO modifyModelVm(ModelVMDTO modelVMDTO);
}
