package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    /**
     * Used to get the course by the name.
     * @param courseName of the desired course.
     * @return empty optional if the course misses.
     */
    Optional<CourseDTO> getCourse(String courseName);

    /**
     * With the getAllCourses method all courses with all details are returned.
     * @return it returns the list of all courses in the application.
     */
    List<CourseDTO> getAllCourses();

    /**
     * This method is used to get all the courses of the authenticated teacher. In order to perform it a
     * custom query was created in courseRepository.
     * @return it returns all the courses of the authenticated teacher.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<CourseDTO> getTeacherCourses();

    /**
     * @return
     */
    List<CourseDTO> getStudentCourses();

    /**
     * With the addCourse method a new course is added and the teacher who insert it is considered
     * the principal teacher. He/She has the possibility to add other teachers by using the
     * addTeacherToCourse method.
     *
     * @param courseDTO it is the course that must be added.
     * @return it returns the inserted course.
     */
    @PreAuthorize("hasRole('TEACHER')")
    CourseDTO addCourse(CourseDTO courseDTO);

    /**
     * If the course exists and the teacher has the permissions, the course is modified with data
     * received from the client. All fields are updated, even if they are not changed.
     *
     * @param courseDTO it is the course with new data.
     * @return the new courseDTO.
     */
    @PreAuthorize("hasRole('TEACHER')")
    CourseDTO modifyCourse(CourseDTO courseDTO);

    /**
     * With the deleteCourse method the passed course is removed if exists and if the current teacher is one
     * of the teachers of the courses. All the relationship are also deleted, for this reason a copy of the
     * teachers list is taken in order to delete them from the course.
     *
     * @param courseName it is the name of the course that must be deleted.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteCourse(String courseName);




}
