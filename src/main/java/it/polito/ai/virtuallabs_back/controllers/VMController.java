package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import it.polito.ai.virtuallabs_back.services.VMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/vms")
public class VMController {

    @Autowired
    VMService vmService;

    @GetMapping("/teams/{teamId}")
    public List<VMDTO> getTeamVms(@PathVariable Long teamId) {
        return vmService.getTeamVms(teamId)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/courses/{courseName}")
    public List<VMDTO> getCourseVms(@PathVariable String courseName) {
        return vmService.getCourseVms(courseName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping("/{teamId}")
    public VMDTO addVm(@Valid @RequestBody VMDTO vmDTO, @PathVariable Long teamId) {
        return ModelHelper.enrich(vmService.addVm(vmDTO, teamId));
    }

    @PutMapping({"", "/"})
    public VMDTO modifyVm(@Valid @RequestBody VMDTO vmDTO) {
        return ModelHelper.enrich(vmService.modifyVm(vmDTO));
    }

    @PutMapping("/{vmId}/onOff")
    public VMDTO onOff(@PathVariable Long vmId) {
        return ModelHelper.enrich(vmService.onOff(vmId));
    }

    @PutMapping("/{vmId}/addOwner/{studentSerial}")
    public VMDTO addOwner(@PathVariable Long vmId, @PathVariable String studentSerial) {
        return ModelHelper.enrich(vmService.addOwner(vmId, studentSerial));
    }

    @DeleteMapping("/{vmId}")
    public void deleteVm(@PathVariable Long vmId) {
        vmService.deleteVm(vmId);
    }
}
