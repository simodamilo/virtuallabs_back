package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;
import it.polito.ai.virtuallabs_back.services.ModelVMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/API/modelVms")
public class ModelVMController {

    @Autowired
    ModelVMService modelVMService;

    @PostMapping("/{teamId}")
    public ModelVMDTO addModelVM(@Valid @RequestBody ModelVMDTO modelVmDTO, @PathVariable Long teamId) {
        return modelVMService.addModelVm(modelVmDTO, teamId);
    }

    @PutMapping({"", "/"})
    public ModelVMDTO modifyModelVM(@Valid @RequestBody ModelVMDTO modelVmDTO) {
        return modelVMService.modifyModelVm(modelVmDTO);
    }
}
