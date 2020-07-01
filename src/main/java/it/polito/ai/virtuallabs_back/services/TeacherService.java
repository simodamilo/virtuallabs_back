package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    /**
     * With the getTeacher method all details about the teacher are returned.
     *
     * @param teacherId it is the id of the searched teacher.
     * @return it returns an optional that could be empty if the teacher is not found.
     */
    Optional<TeacherDTO> getTeacher(String teacherId);

    /**
     * With the getAllTeachers method all teachers details are returned.
     *
     * @return it returns a list of teachers.
     */
    List<TeacherDTO> getAllTeachers();

    /**
     * This method is used to upload the profile image of the teacher.
     *
     * @param image it is the image that must be saved.
     * @return new TeacherDTO
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeacherDTO uploadImage(byte[] image);

}
