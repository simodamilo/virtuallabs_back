package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;
import it.polito.ai.virtuallabs_back.services.ModelVMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("/API/modelVms")
public class ModelVMController {

    @Autowired
    ModelVMService modelVMService;

    @GetMapping("/{courseName}")
    public ModelVMDTO getModelVM(@PathVariable String courseName) {
        return ModelHelper.enrich(modelVMService.getModelVm(courseName));
    }

    @GetMapping("/{modelVmId}/image")
    public byte[] getModelVmContent(@PathVariable Long modelVmId) {
        return modelVMService.getModelVmContent(modelVmId);
    }

    @PostMapping("/{courseName}")
    public ModelVMDTO addModelVM(@Valid @RequestBody ModelVMDTO modelVmDTO, @PathVariable String courseName) {
        return modelVMService.addModelVm(modelVmDTO, courseName);
    }

    @PutMapping("/{modelVmId}")
    public ModelVMDTO addContent(@RequestParam(value = "imageFile") MultipartFile file,
                                 @PathVariable Long modelVmId) {
        if (!Objects.requireNonNull(file.getContentType()).split("/")[0].equals("image")) {
            modelVMService.deleteModelVm(modelVmId);
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File type not supported");
        }
        return modelVMService.addContent(modelVmId, file);
    }

    @DeleteMapping("/{modelVmId}")
    public void deleteModelVM(@PathVariable Long modelVmId) {
        modelVMService.deleteModelVm(modelVmId);
    }
}
