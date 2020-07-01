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

    @GetMapping("/getStudentVm")
    public List<VMDTO> getVmForStudent() {
        return vmService.getVmForStudent()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{teamId}/getTeamVm")
    public List<VMDTO> getVmForTeam(@PathVariable Long teamId) {
        return vmService.getVmForTeam(teamId)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/getAll")
    public List<VMDTO> getVmForCourse(@PathVariable String courseName) {
        return vmService.getVmForCourse(courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }


    @PostMapping("/{teamId}/add")
    public VMDTO addVm(@Valid @RequestBody VMDTO vmDTO, @PathVariable Long teamId) {
        return ModelHelper.enrich(vmService.addVm(vmDTO, teamId));
    }


    @PutMapping("/modify")
    public VMDTO modifyVm(@Valid @RequestBody VMDTO vmDTO) {
        return ModelHelper.enrich(vmService.modifyVm(vmDTO));
    }

    @PutMapping("/{id}/onOff")
    public VMDTO onOff(@PathVariable Long id) {
        return ModelHelper.enrich(vmService.onOff(id));
    }

    @PutMapping("/{id}/addOwner/{serial}")
    public VMDTO addOwner(@PathVariable Long id, @PathVariable String serial) {
        return ModelHelper.enrich(vmService.addOwner(id, serial));
    }


    @DeleteMapping("/{id}/delete")
    public void deleteVm(@PathVariable Long id) {
        vmService.deleteVm(id);
    }

}
