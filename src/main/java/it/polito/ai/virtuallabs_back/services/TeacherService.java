package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    boolean addTeacher(TeacherDTO teacher);

    Optional<TeacherDTO> getTeacher(String teacherId);

    List<TeacherDTO> getAllTeachers();

}