package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ModelVMService {

    /**
     * Used by the student to add a modelVM.
     *
     * @param modelVMDTO which needs to be added.
     * @param teamId     in which the modelVM is added.
     * @return the added modelVM.
     */
    @PreAuthorize("hasRole('STUDENT')")
    ModelVMDTO addModelVm(ModelVMDTO modelVMDTO, Long teamId);

    /**
     * Used by the student to modify a modelVM.
     *
     * @param modelVMDTO which needs to be modified.
     * @return the modified modelVM.
     */
    @PreAuthorize("hasRole('STUDENT')")
    ModelVMDTO modifyModelVm(ModelVMDTO modelVMDTO);
}
