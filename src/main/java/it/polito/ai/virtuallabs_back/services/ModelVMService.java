package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;

public interface ModelVMService {

    /**
     * Used by the student to add a modelVM.
     *
     * @param modelVMDTO which needs to be added.
     * @param teamId     in which the modelVM is added.
     * @return the added modelVM.
     */
    ModelVMDTO addModelVm(ModelVMDTO modelVMDTO, Long teamId);

    /**
     * Used by the student to modify a modelVM.
     * @param modelVMDTO which needs to be modified.
     * @return the modified modelVM.
     */
    ModelVMDTO modifyModelVm(ModelVMDTO modelVMDTO);
}
