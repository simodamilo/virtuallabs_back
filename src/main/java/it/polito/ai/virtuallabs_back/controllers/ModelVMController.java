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

    @GetMapping("/{courseName}")
    public ModelVMDTO getModelVM(@PathVariable String courseName) {
        return modelVMService.getModelVm(courseName);
    }

    @PostMapping("/{courseName}")
    public ModelVMDTO addModelVM(@Valid @RequestBody ModelVMDTO modelVmDTO, @PathVariable String courseName) {
        return modelVMService.addModelVm(modelVmDTO, courseName);
    }

    @PutMapping({"", "/"})
    public ModelVMDTO modifyModelVM(@Valid @RequestBody ModelVMDTO modelVmDTO) {
        return modelVMService.modifyModelVm(modelVmDTO);
    }
}
