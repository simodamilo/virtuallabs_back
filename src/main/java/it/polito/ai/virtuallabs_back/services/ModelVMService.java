package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

public interface ModelVMService {

    /**
     * Used to get a modelVM.
     *
     * @param courseName in which the modelVM is searched.
     * @return the searched modelVM.
     */
    ModelVMDTO getModelVm(String courseName);

    /**
     * Used to get the content of a modelVm by the Id.
     *
     * @param modelVmId of the desired modelVm.
     * @return the content of the modelVm.
     */
    byte[] getModelVmContent(@PathVariable Long modelVmId);

    /**
     * Used by the teacher to add a modelVM.
     *
     * @param modelVMDTO which needs to be added.
     * @param courseName in which the modelVM is added.
     * @return the added modelVM.
     */
    @PreAuthorize("hasRole('TEACHER')")
    ModelVMDTO addModelVm(ModelVMDTO modelVMDTO, String courseName);

    /**
     * Used by the teacher to add the content to a modelVm.
     *
     * @param modelVmId of the modelVm in which the model is added.
     * @param file      content of the modelVm.
     * @return the modified modelVm.
     */
    @PreAuthorize("hasRole('TEACHER')")
    ModelVMDTO addContent(Long modelVmId, MultipartFile file);

    /**
     * Used by the teacher to delete a modelVm.
     *
     * @param modelVmId of the deleted modelVm.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteModelVm(Long modelVmId);
}
