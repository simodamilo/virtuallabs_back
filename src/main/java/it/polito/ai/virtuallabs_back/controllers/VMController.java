package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import it.polito.ai.virtuallabs_back.services.VMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/vms")
public class VMController {

    @Autowired
    VMService vmService;


    @GetMapping("/{id}")
    public VMDTO getOne(@PathVariable Long id) {
        if (!vmService.getVm(id).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "VM with id " + id + "does not exist");

        return ModelHelper.enrich(vmService.getVm(id).get());
    }

    @GetMapping("/teams/{teamId}") //TODO
    public List<VMDTO> getVMForTeam(@PathVariable String courseName) {
        return vmService.getVMForTeam(courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping("/{courseName}/addOne")
    public VMDTO addVm(@Valid @RequestBody VMDTO vmDTO, @PathVariable String courseName) {
        if (!vmService.addVm(vmDTO, courseName))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A vm with id " + vmDTO.getId() + " already exists");

        return ModelHelper.enrich(vmDTO);
    }

    @PutMapping("/modify")
    public VMDTO modifyVm(@Valid @RequestBody VMDTO vmDTO) { // forse le put si possono fare in questo modo
        VMDTO vm = vmService.modifyVm(vmDTO);
        if (vm == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "It is not possible to modify the vm");
        else
            return vm;
    }

    @DeleteMapping("/{id}/delete")
    public void deleteVm(@PathVariable Long id) {
        vmService.deleteVm(id);
    }

}
