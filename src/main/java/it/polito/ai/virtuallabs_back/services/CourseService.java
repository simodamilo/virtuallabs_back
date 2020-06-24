package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface CourseService {

    @PreAuthorize("hasRole('TEACHER')")
    boolean addCourse(CourseDTO course);

    Optional<CourseDTO> getCourse(String name);

    List<CourseDTO> getAllCourses();

    List<StudentDTO> getEnrolledStudents(String courseName);

    List<TeamDTO> getTeamsForCourse(String courseName);

    @PreAuthorize("hasRole('TEACHER')")
    void enableCourse(String courseName);

    @PreAuthorize("hasRole('TEACHER')")
    void disableCourse(String courseName);

    @PreAuthorize("hasRole('TEACHER')")
    boolean addStudentToCourse(String studentId, String courseName);

    @PreAuthorize("hasRole('TEACHER')")
    boolean addTeacherToCourse(String teacherId, String courseName);

    @PreAuthorize("hasRole('TEACHER')")
    List<Boolean> enrollAll(List<String> studentIds, String courseName);

    @PreAuthorize("hasRole('TEACHER')")
    List<Boolean> enrollCsv(Reader r, String courseName);
}
