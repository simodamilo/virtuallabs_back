package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.VMDTO;
import it.polito.ai.virtuallabs_back.entities.*;

public interface UtilityService {

    Assignment getAssignment(Long assignmentId);

    Course getCourse(String courseName);

    ModelVM getModelVm(Long modelVmId);

    Solution getSolution(Long solutionId);

    Student getStudent();

    Teacher getTeacher();

    Team getTeam(Long teamId);

    VM getVm(Long vmId);

    void courseOwnerValid(String courseName);

    void constraintsCheck(VMDTO vmDTO, Long teamId);
}
