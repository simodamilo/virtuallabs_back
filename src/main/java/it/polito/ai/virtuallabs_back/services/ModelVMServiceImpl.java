package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;
import it.polito.ai.virtuallabs_back.entities.ModelVM;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Team;
import it.polito.ai.virtuallabs_back.exception.CourseNotEnabledException;
import it.polito.ai.virtuallabs_back.exception.ModelVMChangeNotValidException;
import it.polito.ai.virtuallabs_back.exception.TeamNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.ModelVMRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ModelVMServiceImpl implements ModelVMService {

    @Autowired
    UtilityService utilityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ModelVMRepository modelVMRepository;

    @Override
    public ModelVMDTO addModelVm(ModelVMDTO modelVMDTO, Long teamId) {
        Team team = utilityService.getTeam(teamId);
        Student student = utilityService.getStudent();

        if (!student.getTeams().contains(team))
            throw new TeamNotFoundException("Team is not a team of the student");

        if (team.getModelVM() != null || team.getVms().size() != 0)
            throw new ModelVMChangeNotValidException("It is not possible to add a new modelVM");

        if (!team.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        ModelVM modelVM = ModelVM.builder()
                .name(modelVMDTO.getName())
                .type(modelVMDTO.getType())
                .team(team)
                .build();

        return modelMapper.map(modelVMRepository.save(modelVM), ModelVMDTO.class);
    }

    @Override
    public ModelVMDTO modifyModelVm(ModelVMDTO modelVMDTO) {
        ModelVM modelVM = utilityService.getModelVm(modelVMDTO.getId());
        Team team = utilityService.getTeam(modelVM.getTeam().getId());

        if (team.getVms().size() != 0)
            throw new ModelVMChangeNotValidException("It is no more possible to modify the ModelVM");

        if (!team.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        modelVM.setName(modelVMDTO.getName());
        modelVM.setType(modelVMDTO.getType());

        return modelMapper.map(modelVM, ModelVMDTO.class);
    }
}
