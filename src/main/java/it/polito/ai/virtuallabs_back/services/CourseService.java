package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    /**
     * Used to get the course by the name.
     *
     * @param courseName of the desired course.
     * @return empty optional if the course misses.
     */
    Optional<CourseDTO> getCourse(String courseName);

    /**
     * Used to get the list of courses of the authenticated teacher.
     *
     * @return list of found courses.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<CourseDTO> getTeacherCourses();

    /**
     * Used to get the list of courses of the authenticated student.
     *
     * @return list of found courses.
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<CourseDTO> getStudentCourses();

    /**
     * Used by the teacher to add a course.
     *
     * @param courseDTO which needs to be added.
     * @return the added course.
     */
    @PreAuthorize("hasRole('TEACHER')")
    CourseDTO addCourse(CourseDTO courseDTO);

    /**
     * Used by the teacher to modify a course.
     *
     * @param courseDTO which needs to be modified.
     * @return the modified course.
     */
    @PreAuthorize("hasRole('TEACHER')")
    CourseDTO modifyCourse(CourseDTO courseDTO);

    /**
     * Used by the teacher to delete a course.
     *
     * @param courseName which needs to be deleted.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteCourse(String courseName);

}
