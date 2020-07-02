package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.TeacherAlreadyOwnerException;
import it.polito.ai.virtuallabs_back.exception.TeacherNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.TeacherRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    UtilityService utilityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TeacherRepository teacherRepository;

    @Override
    public Optional<TeacherDTO> getTeacher(String teacherId) {
        return teacherRepository.findById(teacherId).map(teacher -> modelMapper.map(teacher, TeacherDTO.class));
    }

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TeacherDTO addTeacherToCourse(String teacherId, String courseName) {
        if (!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException("Teacher not found");
        Teacher teacher = teacherRepository.getOne(teacherId);

        utilityService.courseOwnerValid(courseName);
        Course course = utilityService.getCourse(courseName);

        if (!course.addTeacher(teacher))
            throw new TeacherAlreadyOwnerException("Teacher is already owner of this course");

        return modelMapper.map(teacher, TeacherDTO.class);
    }

    @Override
    public TeacherDTO uploadImage(byte[] image) {
        Teacher teacher = utilityService.getTeacher();

        teacher.setImage(/*compressBytes(image)*/image);
        return modelMapper.map(teacher, TeacherDTO.class);
    }
}
