package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    /**
     * Used to get the teacher by the serial.
     *
     * @param teacherSerial of the desired teacher.
     * @return empty optional if the teacher does not exists.
     */
    Optional<TeacherDTO> getTeacher(String teacherSerial);

    /**
     * Used to get the profile image of the teacher by the serial.
     *
     * @param teacherSerial of the desired teacher.
     * @return the searched profile image.
     */
    byte[] getTeacherImage(String teacherSerial);

    /**
     * Used to get the list of all teachers.
     *
     * @return list of found teachers.
     */
    List<TeacherDTO> getAllTeachers(String courseName);

    /**
     * Used to get the owners of the course.
     *
     * @param courseName of the desired course.
     * @return list of found owners.
     */
    List<TeacherDTO> getCourseOwners(String courseName);

    /**
     * Used to add a teacher to the course by another teacher.
     *
     * @param teacherSerial which needs to be added.
     * @param courseName    in which the teacher is added.
     * @return the added teacher.
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeacherDTO addTeacherToCourse(String teacherSerial, String courseName);

    /**
     * Used by the teacher to add a profile image.
     *
     * @param image updated by the teacher.
     * @return the modified teacher.
     */
    @PreAuthorize("hasRole('TEACHER')")
    byte[] uploadImage(byte[] image);

    /**
     * Used to delete the teacher from the course.
     *
     * @param teacherSerial of the deleted teacher.
     * @param courseName    from which teacher is removed.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteTeacherFromCourse(String teacherSerial, String courseName);
}
