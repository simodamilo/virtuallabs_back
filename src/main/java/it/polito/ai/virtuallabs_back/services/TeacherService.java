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
     * @return empty optional if the course misses.
     */
    Optional<TeacherDTO> getTeacher(String teacherSerial);

    /**
     * Used to get the list of all teachers.
     * @return list of found teachers.
     */
    List<TeacherDTO> getAllTeachers();

    /**
     * Used to add a teacher to the course by another teacher.
     * @param teacherSerial which needs to be added.
     * @param courseName in which the teacher is added.
     * @return the added teacher.
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeacherDTO addTeacherToCourse(String teacherSerial, String courseName);

    /**
     * Used to add an image to the teacher.
     * @param image which needs to be added.
     * @return the modified teacher.
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeacherDTO uploadImage(byte[] image);
}
