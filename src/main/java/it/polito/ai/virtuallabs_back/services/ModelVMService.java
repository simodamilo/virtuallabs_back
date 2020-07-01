package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;

public interface ModelVMService {

    /**
     * This method is used to add a ModelVM from which the students of the corresponding team can create the vms.
     *
     * @param modelVMDTO it is the modelVMDTO that must be added.
     * @param teamId     it is the corresponding team.
     * @return it returns the added modelVMDTO.
     */
    ModelVMDTO addModelVm(ModelVMDTO modelVMDTO, Long teamId);

    /**
     * This method is used to modify the modelVM, it is possible only if there are no vms for the team.
     * If students want to modify it, they must delete all the vms before.
     *
     * @param modelVMDTO it is the new modelVMDTO
     * @return it returns the same instance of the modified modelVMDTO
     */
    ModelVMDTO modifyModelVm(ModelVMDTO modelVMDTO);
}
