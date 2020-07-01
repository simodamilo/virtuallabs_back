package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.ModelVMDTO;
import it.polito.ai.virtuallabs_back.entities.ModelVM;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Team;
import it.polito.ai.virtuallabs_back.exception.CourseNotEnabledException;
import it.polito.ai.virtuallabs_back.exception.ModelVMChangeNotValidException;
import it.polito.ai.virtuallabs_back.exception.ModelVMNotFoundException;
import it.polito.ai.virtuallabs_back.exception.TeamNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.ModelVMRepository;
import it.polito.ai.virtuallabs_back.repositories.StudentRepository;
import it.polito.ai.virtuallabs_back.repositories.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ModelVMServiceImpl implements ModelVMService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ModelVMRepository modelVMRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    StudentRepository studentRepository;


    @Override
    public ModelVMDTO addModelVm(ModelVMDTO modelVMDTO, Long teamId) {
        if (!teamRepository.existsById(teamId))
            throw new TeamNotFoundException("Team does not exist");

        Team team = teamRepository.getOne(teamId);
        if (team.getModelVM() != null)
            throw new ModelVMChangeNotValidException("There is already one modelVM for this team");

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername().split("@")[0]);

        if (team.getVms().size() != 0)
            throw new ModelVMChangeNotValidException("It is no more possible to add a new ModelVM");

        if (!student.getTeams().contains(team))
            throw new TeamNotFoundException("Team is not a team of the student");

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
        if (!modelVMRepository.existsById(modelVMDTO.getId()))
            throw new ModelVMNotFoundException("ModelVM does not exist");

        ModelVM modelVM = modelVMRepository.getOne(modelVMDTO.getId());
        Team team = teamRepository.getOne(modelVM.getTeam().getId());

        if (team.getVms().size() != 0)
            throw new ModelVMChangeNotValidException("It is no more possible to modify the ModelVM");

        if (!team.getCourse().isEnabled())
            throw new CourseNotEnabledException("The course is not enabled");

        modelVM.setName(modelVMDTO.getName());
        modelVM.setType(modelVMDTO.getType());

        return modelMapper.map(modelVM, ModelVMDTO.class);
    }

}
