package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface StudentService {

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    boolean addStudent(StudentDTO student);

    List<Boolean> addAll(List<StudentDTO> students);

    List<Boolean> addAllCSV(Reader r);

    List<CourseDTO> getCourses();

    List<StudentDTO> getStudentsInTeams(String courseName);

    List<StudentDTO> getAvailableStudents(String courseName);

    List<TeamDTO> getTeamsForStudent();
}
