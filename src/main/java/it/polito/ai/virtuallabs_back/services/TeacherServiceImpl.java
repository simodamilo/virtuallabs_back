package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.entities.AppUser;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.repositories.TeacherRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    UserService userService;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    ModelMapper modelMapper;


    /**
     * With the addTeacher method a new teacher is registered in the application
     */
    @Override
    public boolean addTeacher(TeacherDTO teacher) { /* forse non servono più questi metodi addTeacher e addStudent */
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_TEACHER");
        AppUser appUser = userService.addUser(roles);
        teacher.setSerial(appUser.getUsername());
        teacherRepository.save(modelMapper.map(teacher, Teacher.class));
        return true;
    }

    /**
     * With the getTeacher method all details about the teacher are returned
     */
    @Override
    public Optional<TeacherDTO> getTeacher(String teacherId) {
        return teacherRepository.findById(teacherId)
                .map(t -> modelMapper.map(t, TeacherDTO.class));
    }

    /**
     * With the getAllTeachers method all teachers details are returned
     */
    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
    }

}
