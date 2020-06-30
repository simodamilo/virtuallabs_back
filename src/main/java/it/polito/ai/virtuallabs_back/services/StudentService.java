package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    /**
     * @param studentId
     * @return
     */
    Optional<StudentDTO> getStudent(String studentId);

    /**
     * @return
     */
    List<StudentDTO> getAllStudents();

    /**
     * @return
     */
    List<CourseDTO> getCourses();

    /**
     * @param courseName
     * @return
     */
    List<StudentDTO> getStudentsInTeams(String courseName);

    /**
     * @param courseName
     * @return
     */
    List<StudentDTO> getAvailableStudents(String courseName);

    /**
     * @return
     */
    List<TeamDTO> getTeamsForStudent();
}
