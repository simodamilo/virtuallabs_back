package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    /**
     * @param teacherId
     * @return
     */
    Optional<TeacherDTO> getTeacher(String teacherId);

    /**
     * Used to get the image of the teacher by the serial.
     *
     * @param teacherSerial of the desired teacher.
     * @return empty optional if the course misses.
     */
    byte[] getTeacherImage(String teacherSerial);

    /**
     * Used to get the list of all teachers.
     *
     * @return list of found teachers.
     */
    List<TeacherDTO> getAllTeachers(String courseName);

    /**
     * @param courseName
     * @return
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
     * Used to add an image to the teacher.
     *
     * @param image which needs to be added.
     * @return the modified teacher.
     */
    @PreAuthorize("hasRole('TEACHER')")
    byte[] uploadImage(byte[] image);

    /**
     * @param teacherSerial
     * @param courseName
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteTeacherFromCourse(String teacherSerial, String courseName);
}
