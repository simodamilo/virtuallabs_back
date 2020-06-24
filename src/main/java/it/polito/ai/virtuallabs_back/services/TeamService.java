package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    boolean addCourse(CourseDTO course);

    Optional<CourseDTO> getCourse(String name);

    List<CourseDTO> getAllCourses();

    @PreAuthorize("hasRole('ADMIN')")
    boolean addStudent(StudentDTO student);

    @PreAuthorize("hasRole('ADMIN')")
    boolean addTeacher(TeacherDTO teacher);

    Optional<TeacherDTO> getTeacher(String teacherId);

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    List<TeacherDTO> getAllTeachers();

    List<StudentDTO> getEnrolledStudents(String courseName);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    boolean addStudentToCourse(String studentId, String courseName);

    @PreAuthorize("hasRole('ADMIN')")
    boolean addTeacherToCourse(String teacherId, String courseName);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    void enableCourse(String courseName);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    void disableCourse(String courseName);

    @PreAuthorize("hasRole('ADMIN')")
    List<Boolean> addAll(List<StudentDTO> students);

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    List<Boolean> enrollAll(List<String> studentIds, String courseName);

    @PreAuthorize("hasRole('ADMIN')")
    List<Boolean> addAndEnroll(Reader r, String courseName);

    @PreAuthorize("hasRole('STUDENT')")
    List<CourseDTO> getCourses();

    @PreAuthorize("hasRole('STUDENT')")
    List<TeamDTO> getTeamsForStudent();

    List<StudentDTO> getMembers(Long teamId);

    @PreAuthorize("hasRole('STUDENT')")
    TeamDTO proposeTeam(String courseName, String name, List<String> memberIds);

    List<TeamDTO> getTeamForCourse(String courseName);

    List<StudentDTO> getStudentsInTeams(String courseName);

    List<StudentDTO> getAvailableStudents(String courseName);

    void enableTeam(Long teamId);

    void evictTeam(Long teamId);

    void clearToken();

}
