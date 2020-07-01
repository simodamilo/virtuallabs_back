package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    /**
     * With the getTeacher method all details about the teacher are returned.
     *
     * @param teacherSerial it is the id of the searched teacher.
     * @return it returns an optional that could be empty if the teacher is not found.
     */
    Optional<TeacherDTO> getTeacher(String teacherSerial);

    /**
     * With the getAllTeachers method all teachers details are returned.
     *
     * @return it returns a list of teachers.
     */
    List<TeacherDTO> getAllTeachers();

    /**
     * With the addTeacherToCourse method a teacher is added to a specific course by the chief teacher.
     *
     * @param teacherSerial it is the serial of the teacher that must be added to the course.
     * @param courseName    it is the course in which the teacher be must be added.
     * @return it returns the teacherDTO if all is ok, otherwise it returns null.
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeacherDTO addTeacherToCourse(String teacherSerial, String courseName);

    /**
     * This method is used to upload the profile image of the teacher
     *
     * @param image it is the image that must be saved.
     * @return new TeacherDTO
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeacherDTO uploadImage(byte[] image);
}
